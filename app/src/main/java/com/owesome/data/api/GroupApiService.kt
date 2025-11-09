package com.owesome.data.api

import com.owesome.data.entities.GroupCompact
import retrofit2.http.GET

interface GroupApiService {
    @GET("/groups")
    suspend fun getGroups(): List<GroupCompact>
}