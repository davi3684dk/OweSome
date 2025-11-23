package com.owesome.data.api.dto

import com.google.gson.annotations.SerializedName
import java.time.Instant

data class SettleRequestDTO (
    @SerializedName("group_id")
    val groupId: Int
)

data class SettlementDTO (
    @SerializedName("ID")
    val id: Int,
    @SerializedName("Amount")
    val amount: Float,
    @SerializedName("PaidAt")
    val paidAt: String?,
    @SerializedName("CreatedAt")
    val createdAt: String,
    @SerializedName("IsConfirmed")
    val isConfirmed: Boolean,
    @SerializedName("Payer")
    val payer: UserDTO,
    @SerializedName("Receiver")
    val receiver: UserDTO
)