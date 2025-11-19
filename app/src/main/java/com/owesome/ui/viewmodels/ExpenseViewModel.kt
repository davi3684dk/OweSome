package com.owesome.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owesome.data.auth.AuthManager
import com.owesome.data.entities.ExpenseCreate
import com.owesome.data.entities.ExpenseShare
import com.owesome.data.entities.ExpenseShareCreate
import com.owesome.data.repository.ExpenseRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.collections.getValue
import kotlin.collections.setValue

class ExpenseUiState {
    var expenseTitle by mutableStateOf("")
    var expenseTitleError by mutableStateOf<String?>(null)

    var totalAmount by mutableStateOf("")
    var totalAmountError by mutableStateOf<String?>(null)

    var userAmount by mutableStateOf("")
    var userAmountError by mutableStateOf<String?>(null)

    var userMap = mutableStateMapOf<Int,Float>()

    var groupId by mutableIntStateOf(-1)

    val selectedUsers = mutableStateListOf<Int>()

    val currentUser by mutableIntStateOf(-1)
}

class ExpenseViewModel (
    private val expenseRepo: ExpenseRepository,
    private val authManager: AuthManager
): ViewModel() {

    var uiState by mutableStateOf(ExpenseUiState())
        private set

    private val _onComplete = Channel<Boolean>()
    val onComplete = _onComplete.receiveAsFlow()

    fun createExpense() {
        viewModelScope.launch {
            val amount = validateTotalAmount(uiState.totalAmount)
            if (amount == null) {
                uiState.totalAmountError = "Total Amount is not a valid number"
                return@launch
            }
            val split = amount / uiState.selectedUsers.size
            val expenseShares = mutableListOf<ExpenseShareCreate>()
            for (user in uiState.selectedUsers) {
                val expenseShareCreate = ExpenseShareCreate(
                    owedBy = user,
                    amount = split
                )
                expenseShares.add(expenseShareCreate)
            }
            val expenseCreate = ExpenseCreate(
                amount = amount,
                description = uiState.expenseTitle,
                groupId = uiState.groupId,
                paidBy = authManager.loggedInUser?.id ?: -1,
                split = expenseShares
            )
            val success = expenseRepo.addExpense(expenseCreate)
            if (success) {
                _onComplete.send(true)
            }
        }
    }

    fun validateTitle(Title: String) {

    }

    fun validateTotalAmount(TotalAmount: String): Float? {
        return try {
            TotalAmount.toFloat()
        } catch (
            e: Exception
        ) {
            null
        }
    }

    fun validateUserAmount(UserAmount: String) {

    }
}


