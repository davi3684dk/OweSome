package com.owesome.ui.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owesome.data.entities.Group
import com.owesome.data.entities.User
import com.owesome.data.repository.GroupRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CreateGroupViewModel(
    private val groupRepository: GroupRepository
) : ViewModel() {
    val maxGroupNameLength = 30

    var groupName by mutableStateOf("")
        private set

    var groupImage by mutableStateOf<Uri?>(null)
        private set

    var users = mutableStateListOf<User>()
        private set

    var groupError by mutableStateOf(false)
        private set

    var imageError by mutableStateOf(false)
        private set

    //Use channel for producer->consumer, and Flows for broadcasting
    private val _groupCreated = Channel<Group>()
    val groupCreated = _groupCreated.receiveAsFlow()

    fun setGroup(group: Group) {
        groupName = group.name
        users.addAll(group.users)
    }

    fun validateFields(): Boolean {
        var valid = true
        if (groupName.isEmpty()) {
            groupError = true
            valid = false
        }
        if (groupImage == null) {
            imageError = true
            valid = false
        }
        return valid
    }

    fun onGroupNameChange(newName: String) {
        if (newName.length <= maxGroupNameLength)
            groupName = newName

        groupError = false
    }

    fun onGroupImageChange(uri: Uri?) {
        groupImage = uri
        imageError = false
    }

    fun addUser(user: User) {
        if (!users.contains(user))
            users.add(user)
    }

    fun createGroup() {
        if (!validateFields())
            return

        viewModelScope.launch {
            val newGroup = groupRepository.createGroup(
                name = groupName,
                description = "",
                users = users
            )
            if (newGroup != null) {
                _groupCreated.send(newGroup)
            }
        }
    }

    fun updateGroup() {
        if (!validateFields())
            return

        viewModelScope.launch {

        }
    }

    fun removeUser(user: User) {
        users.remove(user)
    }
}