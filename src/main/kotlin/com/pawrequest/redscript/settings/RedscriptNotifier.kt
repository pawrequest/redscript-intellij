package com.pawrequest.redscript.settings

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project

fun notifyRedscriptProjectMaybe(project: Project?, text: String, type: NotificationType = NotificationType.INFORMATION, withSettingsLink: Boolean = false) {
    val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("Redscript Notifications")
    val notification = notificationGroup.createNotification(
        "Redscript IDE",
        text,
        type
    )
    if (withSettingsLink) {
        notification.addAction(object : AnAction("Settings") {
            override fun actionPerformed(e: AnActionEvent) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, "Redscript")
            }
        })
    }
    notification.notify(project)
}


//
//fun notifyRedscriptProjectWithSettingsLink(
//    project: Project,
//    text: String,
//    type: NotificationType = NotificationType.INFORMATION
//) {
//    val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("Redscript Notifications")
//    val notification = notificationGroup.createNotification(
//        "Redscript IDE",
//        text,
//        type
//    )
//    notification.addAction(object : AnAction("Settings") {
//        override fun actionPerformed(e: AnActionEvent) {
//            ShowSettingsUtil.getInstance().showSettingsDialog(project, "Redscript")
//        }
//    })
//    notification.notify(project)
//}
//
//
//// Application-wide notification (no project needed)
//fun notifyRedscriptApp(text: String, type: NotificationType = NotificationType.INFORMATION) {
//    val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("Redscript Notifications")
//    val notification = notificationGroup.createNotification(
//        "Redscript IDE",
//        text,
//        type
//    )
//    // Pass null to show notification at application level
//    notification.notify(null)
//}
//
//fun notifyRedscriptAppWithSettingsLink(text: String, type: NotificationType = NotificationType.INFORMATION) {
//    val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("Redscript Notifications")
//    val notification = notificationGroup.createNotification(
//        "Redscript IDE",
//        text,
//        type
//    )
//    notification.addAction(object : AnAction("Settings") {
//        override fun actionPerformed(e: AnActionEvent) {
//            ShowSettingsUtil.getInstance().showSettingsDialog(null, "Redscript")
//        }
//    })
//    notification.notify(null)
//}



//fun notifyRedscriptProject(project: Project, text: String, type: NotificationType = NotificationType.INFORMATION) {
//    val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("Redscript Notifications")
//    val notification = notificationGroup.createNotification(
//        "Redscript IDE",
//        text,
//        type
//    )
//    notification.notify(project)
//}
