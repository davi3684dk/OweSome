package com.owesome.data.repository

import com.owesome.data.api.UserApiService
import com.owesome.data.entities.User
import kotlinx.coroutines.delay

interface UserRepository {
    suspend fun getUserIdByName(username: String): User?
}

class UserRepositoryImpl(
    val userApiService: UserApiService
) : UserRepository {
    override suspend fun getUserIdByName(username: String): User? {
        delay(500)

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
    }
}