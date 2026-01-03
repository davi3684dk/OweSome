package com.owesome.data.api

import com.google.gson.annotations.SerializedName
import com.owesome.data.api.dto.UserDTO
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    @POST("/auth/refresh")
    fun refreshToken(): Call<RefreshResponse>

    @POST("/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("/auth/register")
    suspend fun register(@Body userRequest: RegisterRequest): Response<Unit>

    @POST("/auth/logout")
    suspend fun logout(): Response<Unit>

    @GET("/auth/user")
    suspend fun user(): Response<UserDTO>
}

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val phone: String
)

data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    val user: UserDTO
)
data class UpdateUserPasswordRequest(
    @SerializedName("old_password")
    val oldPassword: String,
    @SerializedName("new_password")
    val newPassword: String
)

data class UpdateUserRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String
)