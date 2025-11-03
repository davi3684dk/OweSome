package com.owesome.data.entities

data class Expense(val id: Int, val amount: Number, val description: String, val groupId: Int, val paidBy: User, val split: List<ExpenseShare>)

data class ExpenseCreate(val amount: Number, val description: String, val groupId: Int, val paidBy: Int, val split: List<ExpenseShareCreate>)
