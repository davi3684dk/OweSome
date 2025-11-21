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
import kotlin.collections.setValue
import kotlin.math.exp

class ExpenseUiState {
    var expenseTitle by mutableStateOf("")
    var expenseTitleError by mutableStateOf<String?>(null)

    var totalAmount by mutableStateOf("")
    var totalAmountError by mutableStateOf<String?>(null)

    var userMap = mutableStateMapOf<Int,Float>()
    var userMapError = mutableStateMapOf<Int,String?>()

    var groupId by mutableIntStateOf(-1)

    val selectedUsers = mutableStateListOf<Int>()

    val currentUser by mutableIntStateOf(-1)

    var customAmount by mutableStateOf(false)
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
            var expenseShares = mutableListOf<ExpenseShareCreate>()
            if (!uiState.customAmount) {
                expenseShares = expenseSharesEven(expenseShares, amount)
            } else {
                if (validateUserAmounts(amount) == null) {
                    return@launch
                }
                expenseShares = expenseCreateCustom(expenseShares, amount)
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

    /*TODO Refactor the two below functions if there is time...
       For loop could likely be moved back up into main function */
    fun expenseSharesEven(
        expenseShares: MutableList<ExpenseShareCreate>,
        amount: Float
    ): MutableList<ExpenseShareCreate> {
        val split = amount / uiState.selectedUsers.size
        for (user in uiState.selectedUsers) {
            val expenseShareCreate = ExpenseShareCreate(
                owedBy = user,
                amount = split
            )
            expenseShares.add(expenseShareCreate)
        }
        return expenseShares
    }

    fun expenseCreateCustom(
        expenseShares: MutableList<ExpenseShareCreate>,
        amount: Float
    ): MutableList<ExpenseShareCreate>  {
        var toPay = amount
        for (entry in uiState.userMap) {
            toPay = toPay - entry.value
            val expenseShareCreate = ExpenseShareCreate(
                owedBy = entry.key,
                amount = entry.value
            )
            expenseShares.add(expenseShareCreate)
        }
        if (toPay != 0f) {
            toPay = toPay/(uiState.selectedUsers.size-uiState.userMap.size)
            for (user in uiState.selectedUsers) {
                if (!uiState.userMap.containsKey(user)) {
                    val expenseShareCreate = ExpenseShareCreate(
                        owedBy = user,
                        amount = toPay
                    )
                    expenseShares.add(expenseShareCreate)
                }
            }
        }
        return expenseShares
    }

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

    fun mapUserAmount(user: Int, amount: String) {
        try {
            uiState.userMap.put(user,amount.toFloat())
            if (uiState.userMapError.containsKey(user)) {
                uiState.userMapError.remove(user)
            }
        } catch ( e: Exception ) {
            if (!amount.isEmpty()) {
                uiState.userMapError.put(user,null)
            }
        }
    }

    fun validateUserAmounts(totalAmount: Float): Boolean? {
        if (!uiState.userMapError.isEmpty()) {
            return null
        }
        var tally = 0.0f
        for (entry in uiState.userMap) {
            tally = tally + entry.value
            if (entry.value > totalAmount) {
                return null
            } else if (tally > totalAmount) {
                return null
            } else if (entry.value == 0.0f) {
                return null
            }
        }
        return true
    }
}

/*
TODO Current errors happen when something improper has been typed into a field,
 the validateUserAmounts() will never succeed. Additionally the app crashes if
 the custom amount is the same as the total while another is simply selected
 to share the expense without an amount specified.
 When the amount for two is changed into something that works it will however
 still proceed to work.
 - Will work when custom input for one person is put in first, but not if another
    user have had their custom deleted and selectin removed.
 */
