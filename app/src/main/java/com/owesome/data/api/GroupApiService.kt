package com.owesome.data.api

import com.google.gson.annotations.SerializedName
import com.owesome.data.api.dto.*
import com.owesome.data.entities.Group
import com.owesome.data.entities.GroupCompact
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface GroupApiService {
    @GET("/groups")
    suspend fun getGroups(): Response<GroupListDTO>

    @DELETE("/groups/{id}")
    suspend fun deleteGroup(@Path("id") groupId: String): Response<Unit>

    @GET("/groups/{id}")
    suspend fun getGroup(@Path("id") groupId: String): Response<GroupDTO>

    @POST("/groups")
    suspend fun createGroup(@Body group: CreateGroupDTO): Response<GroupResponseDTO>

    @POST("/groups/{id}/add-member")
    suspend fun addMember(@Path("id") groupId: String, @Body user: AddMemberDTO): ResponseBody

    @POST("/groups/{id}/remove-member")
    suspend fun removeMember(@Path("id") groupId: String, @Body user: AddMemberDTO): ResponseBody

    @PATCH("/groups/{id}")
    suspend fun updateGroup(@Path("id") groupId: String, @Body group: UpdateGroupDTO): Response<GroupResponseDTO>

    @POST("/settlements/create")
    suspend fun settleGroup(@Body settleRequest: SettleRequestDTO): Response<Unit>

    @POST("/settlements/{id}/confirm")
    suspend fun confirmSettlement(@Path("id") settlementId: Int): Response<Unit>
}