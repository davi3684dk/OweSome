package com.owesome.data.api.dto

import com.google.gson.annotations.SerializedName

data class CreateExpenseDTO(
    @SerializedName("amount")
    val amount: Float,
    @SerializedName("group_id")
    val groupId: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("expense_shares")
    val expenseShares: List<CreateExpenseShareDTO>
)

data class CreateExpenseShareDTO(
    @SerializedName("user")
    val user: Int,
    @SerializedName("amount_owed")
    val amount: Float
)