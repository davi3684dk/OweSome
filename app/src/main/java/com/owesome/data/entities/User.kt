package com.owesome.data.entities

data class User(val id: Int, val username: String, val email: String, val phone: String)

data class UserCreate(val username: String, val email: String, val phone: Int, val password: String)