package com.owesome.data.entities

data class ExpenseShare(val id: Int, val expenseId: Int, val owedBy: User, val amount: Float)

data class ExpenseShareCreate(val owedBy: Int, val amount: Float)
