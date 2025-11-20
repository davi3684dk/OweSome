package com.owesome.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owesome.data.entities.User
import com.owesome.data.repository.UserRepository
import kotlinx.coroutines.launch

class AddUserViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    var username by mutableStateOf("")
        private set

    var usernameError by mutableStateOf(false)
        private set

    var usernameSuccess by mutableStateOf(false)
        private set

    var foundUser by mutableStateOf<User?>(null)
        private set

    var loading by mutableStateOf(false)
        private set


    fun onUsernameChange(newName: String) {
        username = newName
        usernameSuccess = false
        usernameError = false
        foundUser = null
    }

    fun searchUser() {
        if (username.isBlank()) {
            usernameError = true
            return
        }

        viewModelScope.launch {
            loading = true
            val user = userRepository.getUserIdByName(username)
            if (user != null) {
                usernameSuccess = true
                foundUser = user
            } else {
                usernameError = true
            }
            loading = false
        }
    }

    suspend fun findUser(username: String): User? {
        return userRepository.getUserIdByName(username)
    }
}