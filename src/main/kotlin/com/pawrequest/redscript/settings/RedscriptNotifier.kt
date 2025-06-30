package com.pawrequest.redscript.settings

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project

fun notifyRedscript(project: Project, text: String, type: NotificationType = NotificationType.INFORMATION) {
    val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("Redscript Notifications")
    val notification = notificationGroup.createNotification(
        "Redscript IDE",
        text,
        type
    )
    notification.notify(project)
}


fun notifyRedscriptWithSettingsLink(
    project: Project,
    text: String,
    type: NotificationType = NotificationType.INFORMATION
) {
    val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("Redscript Notifications")
    val notification = notificationGroup.createNotification(
        "Redscript IDE",
        text,
        type
    )
    notification.addAction(object : AnAction("Settings") {
        override fun actionPerformed(e: AnActionEvent) {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, "Redscript")
        }
    })
    notification.notify(project)
}