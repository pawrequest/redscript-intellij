package com.pawrequest.redscript.settings

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.pawrequest.redscript.server.*
import com.pawrequest.redscript.util.redLog
import javax.swing.JComponent

class RedscriptConfigurable(private val project: Project) : SearchableConfigurable {

    private var redscriptSettingsComponent: RedscriptSettingsComponent? = null
    override fun getId(): String = "redscript.settings"

    override fun getDisplayName(): String = "Redscript"

    override fun createComponent(): JComponent {
        redscriptSettingsComponent = RedscriptSettingsComponent(project)
        return redscriptSettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val ret = redscriptSettingsComponent!!.gameDir != RedscriptSettings.getInstance().gameDir ||
                redscriptSettingsComponent!!.redscriptIDEPath != RedscriptSettings.getInstance().redscriptIDEPath ||
                redscriptSettingsComponent!!.redscriptIDEVersion != RedscriptSettings.getInstance().redscriptIDEVersion
        return ret
    }

    override fun apply() {
        val settings = RedscriptSettings.getInstance()
        val oldGameDir = settings.gameDir
        val oldIDEPath = settings.redscriptIDEPath
        val oldIDEVersion = settings.redscriptIDEVersion

        val newGameDir = redscriptSettingsComponent!!.gameDir ?: System.getenv("REDCLI_GAME")
        val newIDEPath = redscriptSettingsComponent!!.redscriptIDEPath ?: ""
        val newIDEVersion = redscriptSettingsComponent!!.redscriptIDEVersion ?: ""

        var modified = false

        if (oldGameDir != newGameDir) {
            settings.gameDir = newGameDir
            modified = true
        }

        if (newIDEPath != oldIDEPath) {
            settings.redscriptIDEPath = newIDEPath
            if (!newIDEPath.equals(getBinaryPathDefault())) {
                settings.redscriptIDEVersion = "user-custom"
            }
            modified = true
        }

        if (oldIDEVersion != newIDEVersion) {
            settings.redscriptIDEVersion = newIDEVersion
            modified = true
        }



        if (modified) {
            redLog("Redscript settings modified, applying changes...")
            checkGameDirValid(project)
            maybeDownloadRedscriptIdeProject(project, newIDEVersion)
            RedscriptState.binaryUpdateChecked = true
            startRedscriptLanguageServer()
        }
    }


    override fun reset() {
        redscriptSettingsComponent!!.gameDir = RedscriptSettings.getInstance().gameDir
        redscriptSettingsComponent!!.redscriptIDEPath = RedscriptSettings.getInstance().redscriptIDEPath
        redscriptSettingsComponent!!.redscriptIDEVersion = RedscriptSettings.getInstance().redscriptIDEVersion
    }

    override fun disposeUIResources() {
        redscriptSettingsComponent = null
    }
}
