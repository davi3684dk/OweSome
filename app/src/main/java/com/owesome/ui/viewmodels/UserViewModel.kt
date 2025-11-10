package com.owesome.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owesome.data.entities.User
import com.owesome.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    suspend fun findUser(username: String): User? {
        return userRepository.getUserByName(username)
    }
}