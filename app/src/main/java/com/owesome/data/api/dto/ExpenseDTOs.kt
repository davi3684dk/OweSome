package com.owesome.data.api.dto

import com.google.gson.annotations.SerializedName
import java.time.Instant

data class ExpenseShareDTO(
    @SerializedName("ID")
    val id: Int,
    @SerializedName("User")
    val user: UserDTO,
    @SerializedName("AmountOwed")
    val amount: Float
)

data class ExpenseDTO(
    @SerializedName("ID")
    val id: Int,
    @SerializedName("CreatedAt")
    val createdAt: String,
    @SerializedName("Amount")
    val amount: Float,
    @SerializedName("Description")
    val description: String,
    @SerializedName("PaidBy")
    val paidBy: UserDTO,
    @SerializedName("ExpenseShares")
    val expenseShares: List<ExpenseShareDTO>,
    @SerializedName("Status")
    val status: Float,
    @SerializedName("Settled")
    val settled: Boolean
)