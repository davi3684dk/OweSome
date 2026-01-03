package com.owesome.data.api

import com.owesome.data.api.dto.UserDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface UserApiService {
    @GET("/users/{username}")
    suspend fun findUserByName(@Path("username") username: String): UserDTO?

    // this returns a status and a message not a userDTO
    @PATCH("/users/update/{id}/change-password")
    suspend fun updateUserPassword(
        @Path("id") id: Int,
        @Body updateUserPasswordRequest: UpdateUserPasswordRequest
    ) : Response<Unit>

    @PATCH("/users/update/{id}")
    suspend fun updateUserByID(
        @Path("id") id: Int,
        @Body updateUserRequest : UpdateUserRequest
    ) : Response<Unit>
}

