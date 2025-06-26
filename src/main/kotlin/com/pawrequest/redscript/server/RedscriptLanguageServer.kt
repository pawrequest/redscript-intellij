package com.pawrequest.redscript.server

import com.intellij.openapi.project.ProjectManager
import com.pawrequest.redscript.settings.RedscriptSettings
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.ServerStatus
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider

class RedscriptLanguageServer : OSProcessStreamConnectionProvider() {
    init {
        val binaryFile = getRedscriptIDEBinaryPath().toFile()
        if (!binaryFile.exists()) {
            throw IllegalStateException("Redscript IDE binary not found at ${binaryFile.absolutePath}. Please install the Redscript IDE plugin.")
        }
        val commandLine = com.intellij.execution.configurations.GeneralCommandLine(binaryFile.absolutePath)
        super.setCommandLine(commandLine)

    }

    override fun getInitializationOptions(rootUri: com.intellij.openapi.vfs.VirtualFile?): Any {
        val options: MutableMap<String, Any> = java.util.HashMap()
        options["ui.semanticTokens"] = true
        options["game_dir"] = RedscriptSettings.getInstance().gameDir
        return options
    }
}

fun withLanguageServerRestart(action: () -> Unit = {}) {
    println("RESTARTING LANGUAGE SERVER in withLanguageServerRestart()")
    val project = ProjectManager.getInstance().openProjects.firstOrNull()
    if (project != null) {
        val languageServerManager: LanguageServerManager = LanguageServerManager.getInstance(project)
        val redServerStatus = languageServerManager.getServerStatus("redscript.server")
        println("Redscript Server Status = $redServerStatus")
        if (redServerStatus == ServerStatus.started || redServerStatus == ServerStatus.starting) {
            println("Stopping redscript.server in withLanguageServerRestart()")
            languageServerManager.stop("redscript.server")
            var stopped = false
            repeat(100) label@{
//            for (_ in 1..100) {


                println("Waiting for redscript.server to stop...")
                if (languageServerManager.getServerStatus("redscript.server") in setOf(
                        ServerStatus.none,
                        ServerStatus.stopped
                    )
                ) {
                    println("Success: Server Stopped or none")
                    stopped = true
//                    break
                    return@label
                }
                Thread.sleep(100)
            }
            if (!stopped) {
                println("Failure: Server did not stop in time")
            }

            try {
                action()
            } catch (e: Exception) {
                println("Error during server restart action execution: ${e.message}")
                e.printStackTrace()
            } finally {
                println("Starting redscript.server")
                languageServerManager.start("redscript.server")
            }
        }
    }
}
