package com.owesome.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.owesome.data.repository.GroupRepository

class GroupViewModel(private val repository: GroupRepository): ViewModel() {
    fun getText(): String {
        return "Group View Model";
    }
}