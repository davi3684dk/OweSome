package com.owesome.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owesome.data.entities.User
import com.owesome.data.repository.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProfileUiState {
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var phonePrefix by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var oldPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmNewPassword by mutableStateOf("")

    var usernameError by mutableStateOf<String?>(null)
    var phoneNumberError by mutableStateOf<String?>(null)
    var emailError by mutableStateOf<String?>(null)
    var oldPasswordError by mutableStateOf<String?>(null)
    var newPasswordError by mutableStateOf<String?>(null)
    var confirmNewPasswordError by mutableStateOf<String?>(null)

    var errorMsg by mutableStateOf("")
}

class ProfileViewModel(
    private val userRepo: UserRepository
): ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    private val _onUpdateDetails = Channel<Boolean>()
    val onUpdateDetails = _onUpdateDetails.receiveAsFlow()

    private val _onUpdatePassword = Channel<Boolean>()
    val onUpdatePassword = _onUpdatePassword.receiveAsFlow()

    fun changeAccontDetails() {
        uiState.usernameError = validateUsername(uiState.username)
        uiState.emailError = validateEmail(uiState.email)

        if (uiState.usernameError == null && uiState.emailError == null) {
            // Validation check complete, update account details
            viewModelScope.launch {
                val result = userRepo.updateUser(
                    user = User(
                        1, //How do i get user id the best way?
                        uiState.username,
                        uiState.email,
                        uiState.phoneNumber
                    )
                )

                // Validate that request was succesful and send to screen
                /*if (result) {
                    _onUpdateDetails.send(true)
                }
                else {
                    uiState.errorMsg = "Account Update Failed: $(result.Error)"
                }*/
            }
        }
    }

    fun changePassword() {
        uiState.newPasswordError = validateNewPassword(uiState.newPassword)
        uiState.confirmNewPasswordError = validateConfirmNewPassword(uiState.newPassword, uiState.confirmNewPassword)

        if (uiState.newPasswordError == null && uiState.confirmNewPasswordError == null) {
            // Validation check complete, update password
            // TODO: Validate if input old password is correct

            viewModelScope.launch {
                // TODO: Call API to change password

            }
        }
    }

    // Helper functions for validation.
    fun validateUsername(username: String): String? {
        val usernameRegex = "^[A-Za-z0-9_-]{4,16}$"
        return when {
            username.isBlank() -> "New username cannot be empty"
            !Regex(usernameRegex).matches(username) -> "New username must be between 4-16 characters and only include normal letters A-Z, numbers, dashes (-) and underscores (_)"
            else -> null
        }
    }

    fun validateEmail(email: String): String? {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return when {
            email.isBlank() -> "New email cannot be empty"
            !Regex(emailRegex).matches(email) -> "Invalid email"
            else -> null
        }
    }

    fun validateNewPassword(password: String): String? {
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
        return when {
            password.isBlank() -> "New Password cannot be empty"
            !Regex(passwordRegex).matches(password) ->
                "New password must be 8+ chars and include at least 1 of each: upper, lower, digit & special character"
            else -> null
        }
    }

    fun validateConfirmNewPassword(newPassword: String, confirmNewPassword: String): String? {
        return when {
            confirmNewPassword.isBlank() -> "Confirm new password cannot be empty"
            confirmNewPassword != newPassword -> "New passwords do not match"
            else -> null
        }
    }
}