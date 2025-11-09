package com.owesome.data.api

import com.owesome.data.entities.Group
import com.owesome.data.entities.GroupCompact
import retrofit2.http.GET
import retrofit2.http.Path

interface GroupApiService {
    @GET("/groups")
    suspend fun getGroups(): List<GroupCompact>

    @GET("/group/{id}")
    suspend fun getGroup(@Path("id") groupId: String): Group?
}