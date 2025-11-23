package com.owesome.ui.viewmodels

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owesome.data.auth.AuthManager
import com.owesome.data.entities.User
import com.owesome.data.entities.UserCreate
import com.owesome.data.repository.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/*NOTE: On API call use:
viewModelScope.launch {
    val newGroup = groupRepository.createGroup(
        name = uiState.groupName,
        description = "",
        users = uiState.users
    )
    if (newGroup != null) {
        _onComplete.send(newGroup)
    }
}
 */
class LoginUiState {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMsg by mutableStateOf<String?>(null)

    var failed by mutableStateOf(false)
}

class LoginViewModel(
    private val userRepo: UserRepository,
    private val authManager: AuthManager
): ViewModel() {
    var uiState by mutableStateOf(LoginUiState())
        private set

    private val _onComplete = Channel<User>()
    val onComplete = _onComplete.receiveAsFlow()

    init {
        /*viewModelScope.launch {
            val user = userRepo.getUser()

            if (user != null) {
                authManager.setCurrentUser(user)
                _onComplete.send(user)
            }
        }*/
    }

    fun loginUser() {
        if (uiState.username.isNotBlank() && uiState.password.isNotBlank()) {
            viewModelScope.launch {
                val newUser = userRepo.loginUser(
                    username = uiState.username,
                    password = uiState.password
                )
                // Find out if logged in successful
                if (newUser != null) {
                    authManager.setCurrentUser(newUser)
                    _onComplete.send(newUser)
                }
                else {
                    uiState.errorMsg = "Login failed: Invalid username and password"
                }
            }
        } else {
            uiState.errorMsg = "Please enter both username and password"
        }
    }



}