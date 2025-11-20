package com.owesome.data.entities

enum class NotificationType {
    Info,
    Alert
}

data class Notification(
    val message: String,
    val messageType: NotificationType
)