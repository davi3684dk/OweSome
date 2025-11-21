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

    var userMap = mutableStateMapOf<Int,Float>()
    var userMapError = mutableStateMapOf<Int,String?>()

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
            val title = validateTitle(uiState.expenseTitle)
            if (title == null) {
                uiState.expenseTitleError = "Title Cannot be empty"
                return@launch
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
                uiState.userMapError.put(user,"Invalid Amount")
            } else {
                uiState.userMapError.remove(user)
                uiState.userMap.remove(user)
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
        if (tally != totalAmount) {
            return null
        }
        return true
    }

    /*
    TODO New refactored methods for better performance and readability (hopefully)
     total amount will here not be taken into account when custom amounts are enabled
     - to use only the switching the createExpense on screen and map inputs should work.
     */

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
            if (!uiState.customAmount) {
                val amount = validateTotalAmount(uiState.totalAmount)
                if (amount == null) {
                    uiState.totalAmountError = "Total Amount is not a valid number"
                    return@launch
                }
                val success = expenseRepo.addExpense(createEvenExpense(amount))
                if (success) {
                    _onComplete.send(true)
                }
            } else {
                if (validateMap()) {
                    val success = expenseRepo.addExpense(createCustomExpense())
                    if (success) {
                        _onComplete.send(true)
                    }
                }
            }
        }
    }

}
