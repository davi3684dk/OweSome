package com.owesome.data.api

import com.owesome.data.api.dto.UserDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApiService {
    @GET("/users/{username}")
    suspend fun findUserByName(@Path("username") username: String): UserDTO?
}