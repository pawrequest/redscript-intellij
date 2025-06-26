package com.pawrequest.redscript.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.ServerStatus
import javax.swing.JComponent

class RedscriptConfigurable(private val project: Project) : Configurable {

    private var redscriptSettingsComponent: RedscriptSettingsComponent? = null

    override fun getDisplayName(): String = "Redscript"

    override fun createComponent(): JComponent {
        redscriptSettingsComponent = RedscriptSettingsComponent()
        return redscriptSettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        return redscriptSettingsComponent!!.gameDir != RedscriptSettings.getInstance().gameDir
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
    }

    private fun restartLanguageServer() {
        val languageServerManager = LanguageServerManager.getInstance(project)
        val redServerStatus = languageServerManager.getServerStatus("redscript.server")
        if (redServerStatus == ServerStatus.started || redServerStatus == ServerStatus.starting) {
            languageServerManager.stop("redscript.server")
            languageServerManager.start("redscript.server")
        }
    }

    override fun reset() {
        redscriptSettingsComponent!!.gameDir = RedscriptSettings.getInstance().gameDir
    }

    override fun disposeUIResources() {
        redscriptSettingsComponent = null
    }
}
