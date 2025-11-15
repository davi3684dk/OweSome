package com.owesome.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owesome.data.entities.Group
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NavViewModel : ViewModel() {
    private var _title = MutableStateFlow("OweSome")
    val title: StateFlow<String> get() = _title

    var settingsIcon by mutableStateOf<ImageVector?>(null)
        private set

    private val _settingsPressed = MutableSharedFlow<Unit>()
    val settingsPressed = _settingsPressed.asSharedFlow()

    fun setTitle(title: String, settingsIcon: ImageVector? = null) {
        _title.value = title
        println("Setting title")

        this.settingsIcon = settingsIcon
    }

    fun settingsPressed() {
        viewModelScope.launch {
            _settingsPressed.emit(Unit)
        }
    }
}