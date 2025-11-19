package com.owesome.data.api.dto

import com.google.gson.annotations.SerializedName

data class UserDTO(
    @SerializedName("ID")
    val id: Int,
    @SerializedName("Username")
    val username: String,
    @SerializedName("Email")
    val email: String,
    @SerializedName("Phone")
    val phone: String
)