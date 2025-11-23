package com.owesome.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class Alert(
    val title: String,
    val message: String,
    val dismissText: String?,
    val confirmText: String?,
    val onDismiss: (() -> Unit)?,
    val onConfirm: (() -> Unit)?
)

class AlertManager {

    private var _alert = MutableStateFlow<Alert?>(null)
    val alert = _alert.asStateFlow()

    fun hideAlert() {
        _alert.value = null
    }

    fun showAlert(title: String, message: String, onDismiss: () -> Unit) {
        _alert.value = Alert(title, message, "Ok", null, onDismiss, null)
    }

    fun showYesNoAlert(title: String, message: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
        _alert.value = Alert(title, message, "No", "Yes", onDismiss, onConfirm)
    }
}