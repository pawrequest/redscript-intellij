package com.pawrequest.redscript.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.ProjectManager
import com.redhat.devtools.lsp4ij.LanguageServerManager
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
        val project = ProjectManager.getInstance().openProjects.firstOrNull()
        if (project != null) {
            val languageServerManager = LanguageServerManager.getInstance(project)
            languageServerManager.stop("textmate")
            languageServerManager.start("textmate")
        }
    }

    override fun reset() {
        redscriptSettingsComponent!!.gameDir = RedscriptSettings.getInstance().gameDir
    }

    override fun disposeUIResources() {
        redscriptSettingsComponent = null
    }
}
