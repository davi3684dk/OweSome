package com.owesome.ui.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owesome.data.entities.Expense
import com.owesome.data.entities.ExpenseShare
import com.owesome.data.entities.Group
import com.owesome.data.entities.GroupCompact
import com.owesome.data.entities.User
import com.owesome.data.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupViewModel(private val repository: GroupRepository): ViewModel() {
    private var _currentGroup = MutableStateFlow(Group(
        -1, "", "", listOf(), listOf(), 0f, null
    ))
    val currentGroup: StateFlow<Group> get() = _currentGroup

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getAllGroups(): List<GroupCompact> {
        val groups: MutableList<GroupCompact> = mutableListOf()
        _isLoading.value = true
        viewModelScope.launch {
            val rawGroups = repository.getAllGroups()
            if (rawGroups.isEmpty()) {
                return@launch
            }
            groups.addAll(rawGroups)
            _isLoading.value = false
        }
        return groups
    }

    fun setGroup(groupId: String) {
        _isLoading.value = true

        viewModelScope.launch {
            val rawGroup = repository.getGroup(groupId)
            if (rawGroup != null) {
                _currentGroup.value = rawGroup
            }
            _isLoading.value = false
        }
    }

    fun setGroup(group: Group) {
        _currentGroup.value = group
    }
}