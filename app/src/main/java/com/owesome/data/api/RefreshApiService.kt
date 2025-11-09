package com.owesome.data.api

import retrofit2.Call
import retrofit2.http.POST

interface RefreshApiService {
    @POST("/auth/refresh")
    fun refreshToken(): Call<RefreshResponse>
}