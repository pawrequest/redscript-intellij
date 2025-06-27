package com.pawrequest.redscript.server

import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.pawrequest.redscript.settings.RedscriptSettings
import com.pawrequest.redscript.settings.notifyRedscript
import com.pawrequest.redscript.util.logInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun checkGameDirValid(project: Project) {
    val settings = RedscriptSettings.getInstance()
    if (!gameDirValid(settings.gameDir)) {
        val message =
            "Invalid game directory: \\`${settings.gameDir}\\`. Please set a valid game directory in the Redscript settings."
        ApplicationManager.getApplication().invokeLater {
            notifyRedscript(project, message, type = NotificationType.WARNING)
//            warn(project, message)
        }
    }
}


class RedscriptInitializer : ProjectActivity {
    override suspend fun execute(project: Project) {
        CoroutineScope(Dispatchers.IO).launch {
            checkGameDirValid(project)

            if (!RedscriptBinaryState.isChecked) {
                logInfo("Binary unchecked")
                maybeDownloadRedscriptIde(project, getRedIDEVersionSettings())
                RedscriptBinaryState.isChecked = true
            }
        }
    }
}

//import com.intellij.openapi.project.Project
//import com.intellij.openapi.startup.ProjectActivity
//import com.pawrequest.redscript.settings.RedscriptSettings
//import com.pawrequest.redscript.settings.warn
//import com.pawrequest.redscript.util.logInfo
//
//class RedscriptInitializer : ProjectActivity {
//    override suspend fun execute(project: Project) {
//        val settings = RedscriptSettings.getInstance()
//        if (!gameDirValid(settings.gameDir)) {
//            val message =
//                "Invalid game directory: '${settings.gameDir}'. Please set a valid game directory in the Redscript settings."
//            warn(project, message)
//        }
//
//        if (!RedscriptBinaryState.isChecked) {
//            logInfo("Binary unchecked")
//            maybeDownloadRedscriptIde(project, getRedIDEVersionSettings())
//            RedscriptBinaryState.isChecked = true
//        }
//    }
//}

