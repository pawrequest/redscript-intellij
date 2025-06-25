package com.pawrequest.redscript.server

import com.intellij.openapi.project.ProjectManager
import com.pawrequest.redscript.settings.RedscriptSettings
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.ServerStatus
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider

class RedscriptLanguageServer : OSProcessStreamConnectionProvider() {
    init {
        val binaryFile = getRedscriptIDEBinaryPath().toFile()
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
            for (i in 1..100) {
                println("Waiting for redscript.server to stop...")
                if (languageServerManager.getServerStatus("redscript.server") in setOf(
                        ServerStatus.none,
                        ServerStatus.stopped
                    )
                ) {
                    println("Success: Server Stopped or none")
                    stopped = true
                    break
                }
                Thread.sleep(200)
            }
            if (!stopped) {
                println("Failure: Server did not stop in time")
            }
//            Thread.sleep(2000)
//            val deleted = binaryFile.delete()
//            if (deleted) {
//                println("Redscript binary ${binaryFile} deleted successfully")
//            } else {
//                println("Failed to delete redscript binary ${binaryFile}")
//            }
//            var unlocked = false


//            for (i in 1..100) {
//                println("Waiting for redscript IDE binary file to be unlocked...")
//                try {
//                    FileOutputStream(binaryFile, true).close()
//                    println("Success: file is not locked")
//                    unlocked = true
//                    break
//                } catch (e: Exception) {
//                    Thread.sleep(200)
//                }
//            }
//            if (!unlocked) {
//                println("Failure: file not unlocked in time")
//            }

            try {
                action()
            } finally {
                println("Starting redscript.server")
                languageServerManager.start("redscript.server")
            }
        }
    }
}
