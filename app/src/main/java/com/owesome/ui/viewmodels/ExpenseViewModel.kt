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
import com.owesome.data.entities.Expense
import com.owesome.data.entities.ExpenseCreate
import com.owesome.data.entities.ExpenseShare
import com.owesome.data.entities.ExpenseShareCreate
import com.owesome.data.repository.ExpenseRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.collections.getValue
import kotlin.collections.mutableListOf
import kotlin.collections.setValue
import kotlin.math.exp

class ExpenseUiState {
    var expenseTitle by mutableStateOf("")
    var expenseTitleError by mutableStateOf<String?>(null)

    var totalAmount by mutableStateOf("")
    var totalAmountError by mutableStateOf<String?>(null)

    var newUserMap = mutableStateMapOf<Int,String>()
    var validatedUserMap = mutableStateMapOf<Int,Float>()

    var groupId by mutableIntStateOf(-1)

    val selectedUsers = mutableStateListOf<Int>()
    val currentUser by mutableIntStateOf(-1)

    var customAmount by mutableStateOf(false)
    val maxTitleLength: Int = 30
}

class ExpenseViewModel (
    private val expenseRepo: ExpenseRepository,
    private val authManager: AuthManager
): ViewModel() {

    var uiState by mutableStateOf(ExpenseUiState())
        private set

    private val _onComplete = Channel<Boolean>()
    val onComplete = _onComplete.receiveAsFlow()

    fun validateTitle(title: String): String? {
        return title.ifEmpty {
            null
        }
    }

    fun validateTotalAmount(totalAmount: String): Float? {
        return try {
            totalAmount.toFloat()
        } catch ( e: Exception ) {
            null
        }
    }

    fun validateMap(): Boolean {
        for (entry in uiState.newUserMap) {
            try {
                val amount = entry.value.toFloat()
                uiState.validatedUserMap.put(entry.key,amount)
                if (amount == 0f) { return false }
            } catch ( e: Exception ) {
                return false
            }
        }
        return true
    }

    fun createCustomExpense(): ExpenseCreate  {
        val expenseShares = mutableListOf<ExpenseShareCreate>()
        var sum = 0f
        for (entry in uiState.validatedUserMap) {
            sum = sum + entry.value
            expenseShares.add(createExpenseShare(entry.key, entry.value))
        }
        return createBasicExpense(sum, expenseShares)
    }

    fun createExpenseShare(owedBy: Int, amount: Float): ExpenseShareCreate {
        val expenseShareCreate = ExpenseShareCreate(
            owedBy = owedBy,
            amount = amount
        )
        return expenseShareCreate
    }

    fun createBasicExpense(
        amount: Float,
        expenseShares: MutableList<ExpenseShareCreate>
    ): ExpenseCreate {
        val expenseCreate = ExpenseCreate(
            amount = amount,
            description = uiState.expenseTitle,
            groupId = uiState.groupId,
            paidBy = authManager.loggedInUser?.id ?: -1,
            split = expenseShares
        )
        return expenseCreate
    }

    fun createEvenExpense(amount: Float): ExpenseCreate {
        val expenseShares = mutableListOf<ExpenseShareCreate>()
        val split = amount / uiState.selectedUsers.size
        for (user in uiState.selectedUsers) {
            expenseShares.add(createExpenseShare(user, split))
        }
        return createBasicExpense(amount, expenseShares)
    }

    fun newCreateExpense() {
        viewModelScope.launch {
            val title = validateTitle(uiState.expenseTitle)
            if (title == null) {
                uiState.expenseTitleError = "Title Cannot be empty"
                return@launch
            }
            var success = false
            if (!uiState.customAmount) {
                val amount = validateTotalAmount(uiState.totalAmount)
                if (amount == null) {
                    uiState.totalAmountError = "Total Amount is not a valid number"
                    return@launch
                }
                success = expenseRepo.addExpense(createEvenExpense(amount))
            } else {
                if (validateMap()) {
                    success = expenseRepo.addExpense(createCustomExpense())
                }
            }
            if (success) {
                _onComplete.send(true)
            }
        }
    }
}
