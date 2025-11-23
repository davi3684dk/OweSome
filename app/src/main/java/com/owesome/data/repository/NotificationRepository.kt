package com.owesome.data.repository

import com.owesome.data.api.NotificationApiService
import com.owesome.data.api.mappers.toNotification
import com.owesome.data.entities.Notification

interface NotificationRepository {
    suspend fun getNewNotification(): List<Notification>?
}

class NotificationRepositoryImpl(
    val notificationApiService: NotificationApiService
): NotificationRepository {
    override suspend fun getNewNotification(): List<Notification>? {
        val response = notificationApiService.getNewNotifications()

        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body.map {
                it.toNotification()
            }
        } else {
            null
        }
    }
}