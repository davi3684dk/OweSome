package com.owesome.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owesome.data.entities.Group
import com.owesome.data.entities.GroupCompact
import com.owesome.data.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupViewModel(private val repository: GroupRepository): ViewModel() {
    private var _currentGroup = MutableStateFlow(Group(
        0, "", "", listOf(), listOf(), 0f
    ))
    val currentGroup: StateFlow<Group> get() = _currentGroup

    private var _groups = MutableStateFlow<List<GroupCompact>>(listOf())
    val groups: StateFlow<List<GroupCompact>> get() = _groups

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchGroups()
    }

    fun fetchGroups() {
        _isLoading.value = true
        viewModelScope.launch {
            _groups.value = repository.getAllGroups()
            _isLoading.value = false
        }
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
}