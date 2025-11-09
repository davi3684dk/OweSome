package com.owesome.data.entities

data class User(val id: Int, val username: String, val email: String, val phone: Int)

data class UserCreate(val username: String, val email: String, val phone: Int, val password: String)