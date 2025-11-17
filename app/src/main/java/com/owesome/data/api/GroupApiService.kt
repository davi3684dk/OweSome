package com.owesome.data.api

import com.google.gson.annotations.SerializedName
import com.owesome.data.api.dto.*
import com.owesome.data.entities.Group
import com.owesome.data.entities.GroupCompact
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GroupApiService {
    @GET("/groups")
    suspend fun getGroups(): GroupListDTO

    @GET("/groups/{id}")
    suspend fun getGroup(@Path("id") groupId: String): GroupDTO?

    @POST("/groups")
    suspend fun createGroup(@Body group: CreateGroupDTO): GroupResponseDTO?

    @POST("/groups/{id}/add-member")
    suspend fun addMember(@Path("id") groupId: String, @Body user: AddMemberDTO): ResponseBody
}