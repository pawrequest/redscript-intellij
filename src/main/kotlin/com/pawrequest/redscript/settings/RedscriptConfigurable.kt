package com.pawrequest.redscript.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.ProjectManager
import com.pawrequest.redscript.server.downloadRedscriptIdeFromGithub
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.ServerStatus
import javax.swing.JComponent

class RedscriptConfigurable : Configurable {
    private var redscriptSettingsComponent: RedscriptSettingsComponent? = null

    override fun getDisplayName(): String {
        return "Redscript"
    }

    override fun createComponent(): JComponent {
        redscriptSettingsComponent = RedscriptSettingsComponent()
        return redscriptSettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        return redscriptSettingsComponent!!.gameDir != RedscriptSettings.getInstance().gameDir ||
               redscriptSettingsComponent!!.redscriptIDEVersion != RedscriptSettings.getInstance().redscriptIDEVersion
    }


    override fun apply() {
        val oldGameDir = RedscriptSettings.getInstance().gameDir
        val newGameDir = redscriptSettingsComponent!!.gameDir
        if (newGameDir != null) {
            RedscriptSettings.getInstance().gameDir = newGameDir
        }

        if (newGameDir != oldGameDir) {
            restartLanguageServer()
        }

        val oldIDEVersion = RedscriptSettings.getInstance().redscriptIDEVersion
        val newIDEVersion = redscriptSettingsComponent!!.redscriptIDEVersion
        RedscriptSettings.getInstance().redscriptIDEVersion = newIDEVersion
        if (newIDEVersion != oldIDEVersion) {
            println("Redscript IDE version set to: $newIDEVersion - redownloading binary")
            downloadRedscriptIdeFromGithub()
        }
    }

    private fun restartLanguageServer() {
        println("RESTARTING LANGUAGE SERVER in restartLanguageServer()")
        val project = ProjectManager.getInstance().openProjects.firstOrNull()
        if (project != null) {
            val languageServerManager : LanguageServerManager = LanguageServerManager.getInstance(project)
            val redServerStatus  = languageServerManager.getServerStatus("redscript.server")
            println("Redscript Server Status = $redServerStatus")
            if (redServerStatus == ServerStatus.started || redServerStatus == ServerStatus.starting) {
                println("Stopping redscript.server in restartLanguageServer()")
                languageServerManager.stop("redscript.server")
                println("Starting redscript.server in restartLanguageServer()")
                languageServerManager.start("redscript.server")
            }
        }
    }

    override fun reset() {
        redscriptSettingsComponent!!.gameDir = RedscriptSettings.getInstance().gameDir
        redscriptSettingsComponent!!.redscriptIDEVersion = RedscriptSettings.getInstance().redscriptIDEVersion
    }

    override fun disposeUIResources() {
        redscriptSettingsComponent = null
    }
}
