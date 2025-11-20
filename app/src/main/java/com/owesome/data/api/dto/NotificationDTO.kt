package com.owesome.data.api.dto

import com.google.gson.annotations.SerializedName

data class NotificationDTO(
    @SerializedName("Message")
    val message: String,
    @SerializedName("UserID")
    val userId: Int
)