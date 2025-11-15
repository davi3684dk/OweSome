package com.owesome.data.repository

import com.owesome.data.entities.User
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.random.nextInt

interface UserRepository {
    suspend fun getUserByName(username: String): User?
}

class UserRepositoryImpl : UserRepository {
    override suspend fun getUserByName(username: String): User? {
        delay(500)

        return User(
            id = Random.nextInt(100000),
            username = username,
            email = "${username}@gmail.com",
            phone = 12345678
        )
    }
}