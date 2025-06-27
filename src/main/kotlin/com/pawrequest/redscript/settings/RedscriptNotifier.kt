package com.pawrequest.redscript.settings

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

fun notifyNewBinaryDownloaded(project: Project, version: String) {
    val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("Redscript Notifications")
    val notification = notificationGroup.createNotification(
        "Redscript IDE Update",
        "New binary version $version downloaded.",
        NotificationType.INFORMATION
    )
    notification.notify(project)
}
fun notifyRedscript(project: Project, text: String, type: NotificationType = NotificationType.INFORMATION) {
    val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("Redscript Notifications")
    val notification = notificationGroup.createNotification(
        "Redscript IDE",
        text,
        type
    )
    notification.notify(project)
}
