package com.owesome.data.entities

data class ExpenseShare(val id: Int, val expenseId: Int, val owedBy: User, val amount: Number)

data class ExpenseShareCreate(val owedBy: Int, val amount: Number)
