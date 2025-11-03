package com.owesome.data.entities

data class CalculatedExpense(val id: Int, val settlementId: Int, val payer: User, val receiver: User, val amount: Number)
