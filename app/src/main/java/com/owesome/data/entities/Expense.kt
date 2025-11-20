package com.owesome.data.entities

data class Expense(
    val id: Int,
    val amount: Float,
    val description: String,
    val groupId: String,
    val paidBy: User,
    val split: List<ExpenseShare>,
    val status: Float
)

data class ExpenseCreate(val amount: Float, val description: String, val groupId: Int, val paidBy: Int, val split: List<ExpenseShareCreate>)
