package com.owesome.ui.viewmodels

import android.net.Uri
import androidx.compose.runtime.IntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owesome.data.entities.Group
import com.owesome.data.entities.User
import com.owesome.data.repository.GroupRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class GroupEditorUiState {
    var groupId by mutableIntStateOf(-1)
    var groupName by mutableStateOf("")
    var groupImage by mutableStateOf<Uri?>(null)
    var imageError by mutableStateOf(false)
    var nameError by mutableStateOf(false)
    var users = mutableStateListOf<User>()

    val maxGroupNameLength: Int = 30
}

class GroupEditorViewModel(
    private val groupRepository: GroupRepository
) : ViewModel() {
    var uiState by mutableStateOf(GroupEditorUiState())
        private set

    //Use channel for producer->consumer, and Flows for broadcasting
    private val _onComplete = Channel<Group>()
    val onComplete = _onComplete.receiveAsFlow()

    fun setGroup(group: Group) {
        uiState.groupName = group.name
        uiState. groupId = group.id
        uiState.users.addAll(group.users)
    }

    fun validateFields(): Boolean {
        var valid = true
        if (uiState.groupName.isEmpty()) {
            uiState.nameError = true
            valid = false
        }
        if (uiState.groupImage == null) {
            uiState.imageError = true
            valid = false
        }
        return valid
    }

    fun onGroupNameChange(newName: String) {
        if (newName.length <= uiState.maxGroupNameLength)
            uiState.groupName = newName

        uiState.nameError = false
    }

    fun onGroupImageChange(uri: Uri?) {
        uiState.groupImage = uri
        uiState.imageError = false
    }

    fun addUser(user: User) {
        if (!uiState.users.contains(user))
            uiState. users.add(user)
    }

    fun createGroup() {
        if (!validateFields())
            return

        viewModelScope.launch {
            val newGroup = groupRepository.createGroup(
                name = uiState.groupName,
                description = "",
                users = uiState.users
            )
            if (newGroup != null) {
                _onComplete.send(newGroup)
            }
        }
    }

    fun updateGroup() {
        if (!validateFields())
            return

        viewModelScope.launch {
            val updatedGroup = groupRepository.updateGroup(
                uiState.groupId,
                uiState.groupName,
                "",
                uiState.users
            )
            if (updatedGroup != null) {
                _onComplete.send(updatedGroup)
            }
        }
    }

    fun removeUser(user: User) {
        uiState.users.remove(user)
    }
}