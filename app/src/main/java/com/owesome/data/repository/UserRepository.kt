package com.owesome.data.repository

import com.owesome.data.api.AuthApiService
import com.owesome.data.api.LoginRequest
import com.owesome.data.api.RegisterRequest
import com.owesome.data.auth.AuthManager
import com.owesome.data.entities.User
import com.owesome.data.entities.UserCreate
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.random.nextInt

interface UserRepository {

    suspend fun loginUser(username: String, password: String): User?

    suspend fun registerUser(user: UserCreate)
    suspend fun getUserByName(username: String): User?
    suspend fun logoutUser()
}

class UserRepositoryImpl(
    val authApiService: AuthApiService,
    val authManager: AuthManager
) : UserRepository {


    override suspend fun loginUser(
        username: String,
        password: String
    ): User? {
        try {
            val response = authApiService.login(loginRequest = LoginRequest(
                username = username,
                password = password
            ))

            //TODO: Response validation, did we log in?

            authManager.saveAccessTokens(response.accessToken, response.refreshToken)
            return User(response.user.id, response.user.username, response.user.email, response.user.phone)
        } catch (e: Exception) { return null}
    }

    override suspend fun registerUser(user: UserCreate) {
        val response = authApiService.register(userRequest = RegisterRequest(
            username = user.username,
            email = user.email,
            password = user.password,
            phone = user.phone
        ))

        //TODO: Response validation, did we actually register?
    }

    override suspend fun getUserByName(username: String): User? {
        delay(500)

        return User(
            id = Random.nextInt(100000),
            username = username,
            email = "${username}@gmail.com",
            phone = "12345678"
        )
    }

    override suspend fun logoutUser() {
        val response = authApiService.logout()

        //TODO: Response validation, are we actually logged out?

        authManager.clearTokens()
    }
}