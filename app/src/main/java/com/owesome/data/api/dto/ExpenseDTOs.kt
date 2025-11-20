package com.owesome.data.api.dto

import com.google.gson.annotations.SerializedName

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
    @SerializedName("Amount")
    val amount: Float,
    @SerializedName("Description")
    val description: String,
    @SerializedName("PaidBy")
    val paidBy: UserDTO,
    @SerializedName("ExpenseShares")
    val expenseShares: List<ExpenseShareDTO>,
    @SerializedName("Status")
    val status: Float
)