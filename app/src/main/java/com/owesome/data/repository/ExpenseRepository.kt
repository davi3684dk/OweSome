package com.owesome.data.repository

import com.owesome.data.api.ExpenseApiService
import com.owesome.data.api.dto.CreateExpenseDTO
import com.owesome.data.api.dto.CreateExpenseShareDTO
import com.owesome.data.entities.ExpenseCreate

interface ExpenseRepository {
    suspend fun addExpense(expense: ExpenseCreate): Boolean
}

class ExpenseRepositoryImpl (
    val expenseApiService: ExpenseApiService
) : ExpenseRepository {
    override suspend fun addExpense(expense: ExpenseCreate): Boolean {

        val response = expenseApiService.newExpense(CreateExpenseDTO(
            amount = expense.amount,
            groupId = expense.groupId,
            description = expense.description,
            expenseShares = expense.split.map {
                CreateExpenseShareDTO(
                    user = it.owedBy,
                    amount = it.amount
                )
            }
        ))

        if (response.isSuccessful) {
            return true
        }

        return false
    }
}
