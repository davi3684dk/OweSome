package com.owesome.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavViewModel : ViewModel() {
    private var _title = MutableStateFlow("OweSome")
    val title: StateFlow<String> get() = _title

    fun setTitle(title: String) {
        _title.value = title
        println("Setting title")
    }
}