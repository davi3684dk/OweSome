package com.owesome.data.repository

import com.owesome.data.api.AuthApiService
import com.owesome.data.api.LoginRequest
import com.owesome.data.api.RegisterRequest
import com.owesome.data.auth.AuthManager
import com.owesome.data.api.UserApiService
import com.owesome.data.api.mappers.toUser
import com.owesome.data.entities.User
import com.owesome.data.entities.UserCreate
import kotlinx.coroutines.delay
import org.json.JSONObject
import retrofit2.HttpException

interface UserRepository {

    suspend fun loginUser(username: String, password: String): User?

    suspend fun registerUser(user: UserCreate): Boolean
    suspend fun getUserIdByName(username: String): User?
    suspend fun logoutUser(): Boolean
    suspend fun getUser(): User?
}

class UserRepositoryImpl(
    val authApiService: AuthApiService,
    val authManager: AuthManager,
    val userApiService: UserApiService
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

            //Response validation, did we log in?
            if (response.isSuccessful) {
                // Check if response body values are not empty
                if (response.body() != null) {
                    authManager.saveAccessTokens(response.body()!!.accessToken, response.body()!!.refreshToken)
                    return User(response.body()!!.user.id, response.body()!!.user.username, response.body()!!.user.email, response.body()!!.user.phone)
                }
                else return null
            }
            else return null
        } catch (e: Exception) { return null}
    }

    override suspend fun registerUser(user: UserCreate): Boolean {

        val response = authApiService.register(userRequest = RegisterRequest(
            username = user.username,
            email = user.email,
            password = user.password,
            phone = user.phone
        ))
        //Response validation, did we actually register?
        if (response.isSuccessful) {
            return true
        }
        else return false
    }

    override suspend fun getUserIdByName(username: String): User? {
        delay(500)

        try {
            val response = userApiService.findUserByName(username)

            return if (response != null) {
                User(
                    id = response.id,
                    username = response.username,
                    email = response.email,
                    phone = response.phone
                )
            } else {
                null
            }
        } catch (e: HttpException) {
            return null
        }
    }

    override suspend fun logoutUser(): Boolean {
        val response = authApiService.logout()

        //Response validation, are we actually logged out?
        if (response.isSuccessful){
            authManager.clearTokens()
            return true
        }
        else return false

    }

    override suspend fun getUser(): User? {
        val response = authApiService.user()

        val body = response.body()

        if (response.isSuccessful && body != null) {
            return body.toUser()
        } else {
            return null
        }
    }
}