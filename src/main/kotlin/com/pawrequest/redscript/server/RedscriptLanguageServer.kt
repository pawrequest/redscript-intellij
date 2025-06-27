package com.pawrequest.redscript.server

import com.intellij.openapi.project.ProjectManager
import com.pawrequest.redscript.settings.RedscriptSettings
import com.pawrequest.redscript.settings.notifyRedscript
import com.pawrequest.redscript.util.logInfo
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.ServerStatus
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider

//class NoOpLanguageServer : OSProcessStreamConnectionProvider() {
//    init {
//        logInfo("NoOpLanguageServer initialized")
//    }
//    override fun start(): Process? = null
//    override fun getInputStream(): InputStream? = null
//    override fun getOutputStream(): OutputStream? = null
//    override fun stop() {}
//}

class RedscriptLanguageServer : OSProcessStreamConnectionProvider() {
    init {
//        val binaryFile = getInstalledBinaryPath().toFile()
        val binaryFile = getRedIDEBinaryPathSettings().toFile()
        if (!binaryFile.exists()) {
            throw IllegalStateException("Redscript IDE binary not found at ${binaryFile.absolutePath}. Please install the Redscript IDE plugin.")
        }
        val commandLine = com.intellij.execution.configurations.GeneralCommandLine(binaryFile.absolutePath)
        super.setCommandLine(commandLine)

    }

    override fun getInitializationOptions(rootUri: com.intellij.openapi.vfs.VirtualFile?): Any {
        val options: MutableMap<String, Any> = java.util.HashMap()
//        options["ui.semanticTokens"] = true
        options["game_dir"] = RedscriptSettings.getInstance().gameDir
        return options
    }
}

fun stopRedscriptLanguageServer() {
    println("stopRedscriptLanguageServer() called")
    val project = ProjectManager.getInstance().openProjects.firstOrNull()
    if (project == null) {
        println("No open project found, cannot stop Redscript language server.")
        return
    }
    val languageServerManager: LanguageServerManager = LanguageServerManager.getInstance(project)
    val redServerStatus = languageServerManager.getServerStatus("redscript.server")
    if (redServerStatus == ServerStatus.stopped || redServerStatus == ServerStatus.none) {
        println("Redscript server is already stopped or not running.")
        return
    }
//    val stopped = false
//    for (i in 1..50) {
    repeat(1) {
        when (redServerStatus) {
            ServerStatus.started, ServerStatus.starting -> {
                println("Redscript server is running, stopping it now.")
                languageServerManager.stop("redscript.server")
                Thread.sleep(200)
            }

            ServerStatus.stopping -> {
                println("Redscript server is stopping, waiting for it to stop...")
                Thread.sleep(200)
            }

            else -> {
                println("Redscript server stopped successfully.")
                return
            }
        }
    }
    val msg = "Failed to stop Redscript server."
    logInfo(msg)
    notifyRedscript(project, msg, type = com.intellij.notification.NotificationType.WARNING)
//    warn(project, msg)
}

fun startRedscriptLanguageServer() {
    println("startRedscriptLanguageServer() called")
    val project = ProjectManager.getInstance().openProjects.firstOrNull()
    if (project != null) {
        val languageServerManager: LanguageServerManager = LanguageServerManager.getInstance(project)
        val redServerStatus = languageServerManager.getServerStatus("redscript.server")
        println("Redscript Server Status = $redServerStatus")
        if (redServerStatus == ServerStatus.started || redServerStatus == ServerStatus.starting) {
            println("Redscript server is already running, stopping it before starting again.")
            languageServerManager.stop("redscript.server")
        }
        println("Starting redscript.server in startRedscriptLanguageServer()")
        languageServerManager.start("redscript.server")
    }
}

//fun withLanguageServerRestart(action: () -> Unit = {}) {
//    println("withLanguageServerRestart() called")
//    val project = ProjectManager.getInstance().openProjects.firstOrNull()
//    if (project != null) {
//        val languageServerManager: LanguageServerManager = LanguageServerManager.getInstance(project)
//        val redServerStatus = languageServerManager.getServerStatus("redscript.server")
//        println("Redscript Server Status = $redServerStatus")
//        if (redServerStatus == ServerStatus.started || redServerStatus == ServerStatus.starting) {
//            println("Stopping redscript.server in withLanguageServerRestart()")
//            languageServerManager.stop("redscript.server")
//            var stopped = false
////            repeat(100) label@{
//            for (i in 1..100) {
//
//
//                println("Waiting for redscript.server to stop...")
//                if (languageServerManager.getServerStatus("redscript.server") in setOf(
//                        ServerStatus.none,
//                        ServerStatus.stopped
//                    )
//                ) {
//                    println("Success: Server Stopped or none after $i iterations")
//                    stopped = true
//                    break
////                    return@label
//                }
//                Thread.sleep(100)
//            }
//            if (!stopped) {
//                println("Failure: Server did not stop after 100 iterations")
//            }
//        }
//
//        try {
//            println("Executing action in withLanguageServerRestart()")
//            action()
//        } catch (e: Exception) {
//            println("Error during server restart action execution: ${e.message}")
//            e.printStackTrace()
//            throw e
//        } finally {
//            println("Starting redscript.server")
//            languageServerManager.start("redscript.server")
//        }
//    }
//}
