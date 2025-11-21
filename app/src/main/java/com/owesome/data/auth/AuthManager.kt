package com.owesome.data.auth

import android.content.Context
import androidx.core.content.edit
import com.owesome.data.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthManager(context: Context) {
    private val _loginRequired = MutableSharedFlow<Unit>(replay = 0)
    val loginRequired = _loginRequired.asSharedFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun getAccessToken(): String? = prefs.getString("access_token", null)
    fun getRefreshToken(): String? = prefs.getString("refresh_token", null)


    fun saveAccessTokens(accessToken: String, refreshToken: String) {
        prefs.edit {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
        }
    }

    fun clearTokens() {
        prefs.edit {
            clear()
        }

        _currentUser.value = null
    }

    fun notifyLoginRequired() {
        CoroutineScope(Dispatchers.Main).launch {
            _loginRequired.emit(Unit)
        }
    }

    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }
}