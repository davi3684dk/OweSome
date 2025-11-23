package com.owesome.data.entities

import java.time.OffsetDateTime

data class Expense(
    val id: Int,
    val createdAt: OffsetDateTime,
    val amount: Float,
    val description: String,
    val groupId: String,
    val paidBy: User,
    val split: List<ExpenseShare>,
    val status: Float,
    val settled: Boolean
)

data class ExpenseCreate(val amount: Float, val description: String, val groupId: Int, val paidBy: Int, val split: List<ExpenseShareCreate>)
