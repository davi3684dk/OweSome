package com.owesome.data.api

import com.owesome.data.entities.UserCreate
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("/auth/refresh")
    fun refreshToken(): Call<RefreshResponse>

    @POST("/auth/login")
    fun login(@Body username: String, @Body password: String)

    @POST("/auth/register")
    fun register(@Body userRequest: UserCreate)
}