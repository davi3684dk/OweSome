package com.owesome.data.repository

import com.owesome.data.api.AuthApiService
import com.owesome.data.api.RegisterRequest
import com.owesome.data.auth.AuthManager
import com.owesome.data.entities.UserCreate

sealed class AuthResult {
    data class Success(val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

interface AuthRepository {
    suspend fun register(user: UserCreate): AuthResult
}

class AuthRepositoryImpl(
    val authApiService: AuthApiService
) : AuthRepository {
    override suspend fun register(user: UserCreate): AuthResult {
        val response = authApiService.register(RegisterRequest(
            username = user.username,
            email = user.email,
            password = user.password,
            phone = user.phone.toString()
        )).execute()

        return if (response.isSuccessful) {
            AuthResult.Success(response.message())
        } else {
            AuthResult.Error(response.message())
        }
    }
}