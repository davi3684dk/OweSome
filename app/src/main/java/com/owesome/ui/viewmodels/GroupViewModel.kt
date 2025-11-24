package com.owesome.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owesome.data.auth.AuthManager
import com.owesome.data.entities.Expense
import com.owesome.data.entities.ExpenseShare
import com.owesome.data.entities.Group
import com.owesome.data.entities.GroupCompact
import com.owesome.data.entities.Settlement
import com.owesome.data.entities.User
import com.owesome.data.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupViewModel(
    private val repository: GroupRepository,
    private val authManager: AuthManager
): ViewModel() {
    val currentUser = authManager.currentUser

    private var _currentGroup = MutableStateFlow(Group(
        "-1", "", "", listOf(), listOf(), 0f, null, listOf(), User(-1, "", "", "")
    ))
    val currentGroup: StateFlow<Group> get() = _currentGroup

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    var groups = mutableStateListOf<GroupCompact>()
        private set

    fun getAllGroups() {
        _isLoading.value = true
        viewModelScope.launch {
            val rawGroups = repository.getAllGroups()
            groups.clear()
            groups.addAll(rawGroups)
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

    fun setGroup(group: Group) {
        _currentGroup.value = group
    }

    fun getGroupMembers(): List<User> {
        return _currentGroup.value.users.filter {
            it.id!= currentUser.value?.id
        }
    }

    fun settleGroup() {
        _isLoading.value = true

        viewModelScope.launch {
            repository.settleGroup(currentGroup.value.id)
            setGroup(currentGroup.value.id)
            _isLoading.value = false
        }
    }

    fun confirmSettlement(settlement: Settlement) {
        _isLoading.value = true

        viewModelScope.launch {
            repository.confirmSettlement(settlement.id)
            setGroup(_currentGroup.value.id)
            _isLoading.value = false
        }
    }

    fun deleteGroup(onComplete: () -> Unit) {
        _isLoading.value = true

        viewModelScope.launch {
            repository.deleteGroup(_currentGroup.value.id)
            getAllGroups() //update group list
            onComplete()

            _isLoading.value = false
        }
    }

    fun leaveGroup(onComplete: () -> Unit) {
        _isLoading.value = true

        viewModelScope.launch {
            repository.removeUser(_currentGroup.value.id, currentUser.value?.id ?: -1)
            getAllGroups() //update group list
            onComplete()

            _isLoading.value = false
        }
    }
}