package com.owesome.data.api

import com.owesome.data.api.dto.CreateExpenseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ExpenseApiService {

    @POST("/expenses")
    suspend fun newExpense(@Body expense: CreateExpenseDTO): Response<Unit>
}