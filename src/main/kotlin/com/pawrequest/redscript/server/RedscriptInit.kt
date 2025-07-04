package com.pawrequest.redscript.server

import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.pawrequest.redscript.settings.RedscriptSettings
import com.pawrequest.redscript.settings.getCacheDir
import com.pawrequest.redscript.settings.notifyRedscriptProjectMaybe
import com.pawrequest.redscript.util.redLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.util.logging.Level

object RedscriptState {
    var binaryUpdateChecked: Boolean = false
//    var isInitialized: Boolean = false
}

fun checkGameDirValid(project: Project) {
    val settings = RedscriptSettings.getInstance()
    if (!gameDirValid(settings.gameDir)) {
        val message =
            "Invalid game directory: \\`${settings.gameDir}\\`. Please set a valid game directory in the Redscript settings."
        ApplicationManager.getApplication().invokeLater {
            notifyRedscriptProjectMaybe(project, message, type = NotificationType.ERROR, withSettingsLink = true)
//            notifyRedscriptProjectWithSettingsLink(project, message, type = NotificationType.ERROR)
            redLog(message, Level.SEVERE)
        }
    }
}


class RedscriptInitializer : ProjectActivity {
    override suspend fun execute(project: Project) {
        CoroutineScope(Dispatchers.IO).launch {
            Files.createDirectories(getCacheDir())
            checkGameDirValid(project)

            if (!RedscriptState.binaryUpdateChecked) {
                redLog("Binary unchecked")
                maybeDownloadRedscriptIdeProject(project)
                RedscriptState.binaryUpdateChecked = true
            }
//            contentRootsActivity(project)
        }
    }
}

