package com.owesome.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class Alert(
    val title: String,
    val message: String,
    val onDismiss: () -> Unit
)

class AlertManager {

    private var _alert = MutableStateFlow<Alert?>(null)
    val alert = _alert.asStateFlow()

    fun showAlert(title: String, message: String, onDismiss: () -> Unit) {
        _alert.value = Alert(title, message, onDismiss)
    }
}