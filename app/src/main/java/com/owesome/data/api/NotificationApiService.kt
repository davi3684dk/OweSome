package com.owesome.data.api

import com.owesome.data.api.dto.NotificationDTO
import retrofit2.Response
import retrofit2.http.GET

interface NotificationApiService {
    @GET("/notifications")
    suspend fun getNewNotifications(): Response<List<NotificationDTO>>
}