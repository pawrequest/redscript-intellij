package com.pawrequest.redscript.server

import com.intellij.notification.NotificationType
import com.intellij.openapi.project.ProjectManager
import com.pawrequest.redscript.settings.RedscriptSettings
import com.pawrequest.redscript.settings.getRedIDEBinaryPathSettings
import com.pawrequest.redscript.settings.notifyRedscriptProjectMaybe
import com.pawrequest.redscript.util.redLog
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.ServerStatus
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider
import java.util.logging.Level


class RedscriptLanguageServer : OSProcessStreamConnectionProvider() {
    init {
        var binaryFile = getRedIDEBinaryPathSettings().toFile()
        if (!binaryFile.exists()) {
//            binaryFile = getDefaultBinary()
            val msg = "Redscript IDE binary not found at ${binaryFile.absolutePath}. Please install the Redscript IDE plugin."
            redLog(
                msg,
                Level.SEVERE
            )
            notifyRedscriptProjectMaybe(null,msg, type = NotificationType.ERROR)
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

fun stopRedscriptLanguageServer() {
    redLog("stopRedscriptLanguageServer() called")
    val project = ProjectManager.getInstance().openProjects.firstOrNull()
    if (project == null) {
        redLog("No open project found, cannot stop Redscript language server.")
        return
    }
    val languageServerManager: LanguageServerManager = LanguageServerManager.getInstance(project)
    val redServerStatus = languageServerManager.getServerStatus("redscript.server")
    if (redServerStatus == ServerStatus.stopped || redServerStatus == ServerStatus.none) {
        redLog("Redscript server is already stopped or not running.")
        return
    }
//    val stopped = false
//    for (i in 1..50) {
    repeat(1) {
        when (redServerStatus) {
            ServerStatus.started, ServerStatus.starting -> {
                redLog("Redscript server is running, stopping it now.")
                languageServerManager.stop("redscript.server")
                Thread.sleep(200)
            }

            ServerStatus.stopping -> {
                redLog("Redscript server is stopping, waiting for it to stop...")
                Thread.sleep(200)
            }

            else -> {
                redLog("Redscript server stopped successfully.")
                return
            }
        }
    }
    val msg = "Failed to stop Redscript server."
    redLog(msg, Level.WARNING)
    notifyRedscriptProjectMaybe(project, msg, type = NotificationType.WARNING)
}

fun startRedscriptLanguageServer() {
    redLog("startRedscriptLanguageServer() called")
    val project = ProjectManager.getInstance().openProjects.firstOrNull()
    if (project != null) {
        val languageServerManager: LanguageServerManager = LanguageServerManager.getInstance(project)
        val redServerStatus = languageServerManager.getServerStatus("redscript.server")
        redLog("Redscript Server Status = $redServerStatus")
        if (redServerStatus == ServerStatus.started || redServerStatus == ServerStatus.starting) {
            redLog("Redscript server is already running, stopping it before starting again.")
            languageServerManager.stop("redscript.server")
        }
        redLog("Starting redscript.server in startRedscriptLanguageServer()")
        languageServerManager.start("redscript.server")
    }
}

