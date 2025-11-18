package com.owesome.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owesome.data.entities.User
import com.owesome.data.entities.UserCreate
import com.owesome.data.repository.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RegisterUiState {
    var username by mutableStateOf("")
    var usernameError by mutableStateOf<String?>(null)

    var email by mutableStateOf("")
    var emailError by mutableStateOf<String?>(null)

    var countryCode by mutableStateOf("+45")

    var phoneNumber by mutableStateOf("")
    var phoneNumberError by mutableStateOf<String?>(null)

    var password by mutableStateOf("")
    var passwordError by mutableStateOf<String?>(null)

    var confirmPassword by mutableStateOf("")
    var confirmPasswordError by mutableStateOf<String?>(null)

    var passwordVisible by mutableStateOf(false)
    var confirmPasswordVisible by mutableStateOf(false)
    var expandedDropDown by mutableStateOf(false)
    var errorMsg by mutableStateOf("")
}

class RegisterViewModel(
    private val userRepo: UserRepository
): ViewModel() {

    var uiState by mutableStateOf(RegisterUiState())
        private set

    private val _onComplete = Channel<Boolean>()
    val onComplete = _onComplete.receiveAsFlow()

    fun registerUser() {

        uiState.usernameError = validateUsername(uiState.username)
        uiState.emailError = validateEmail(uiState.email)
        uiState.passwordError = validatePassword(uiState.password)
        uiState.confirmPasswordError = validateConfirmPassword(uiState.password, uiState.confirmPassword)

        if (uiState.usernameError == null
            && uiState.emailError == null
            && uiState.passwordError == null
            && uiState.confirmPasswordError == null
        ) {
            // Validation check complete, all good, register!
            viewModelScope.launch {
                val newUser = userRepo.registerUser(
                    user = UserCreate(
                        uiState.username,
                        uiState.email,
                        uiState.phoneNumber,
                        uiState.password
                    )
                )
                // Find out if user registered succesfully
                if (newUser != null) {
                    _onComplete.send(true)
                }
                else {
                    uiState.errorMsg = "Register failed: unknown error"
                }
            }

        }
    }

    // Helper functions for validation.
    fun validateUsername(username: String): String? {
        val usernameRegex = "^[A-Za-z0-9_-]{4,16}$"
        return when {
            username.isBlank() -> "Username cannot be empty"
            !Regex(usernameRegex).matches(username) -> "Username must be between 4-16 characters and only include normal letters A-Z, numbers, dashes (-) and underscores (_)"
            else -> null
        }
    }

    fun validateEmail(email: String): String? {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return when {
            email.isBlank() -> "Email cannot be empty"
            !Regex(emailRegex).matches(email) -> "Invalid email"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
        return when {
            password.isBlank() -> "Password cannot be empty"
            !Regex(passwordRegex).matches(password) ->
                "Password must be 8+ chars and include at least 1 of each: upper, lower, digit & special character"
            else -> null
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Confirm Password cannot be empty"
            confirmPassword != password -> "Passwords do not match"
            else -> null
        }
    }
}