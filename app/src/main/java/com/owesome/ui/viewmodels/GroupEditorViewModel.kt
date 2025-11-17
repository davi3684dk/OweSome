package com.owesome.ui.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.IntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owesome.data.entities.Group
import com.owesome.data.entities.User
import com.owesome.data.repository.GroupRepository
import com.owesome.util.ImageUtil
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class GroupEditorUiState {
    var groupId by mutableStateOf("")
    var groupName by mutableStateOf("")
    var groupImage by mutableStateOf<ImageBitmap?>(null)
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
        uiState.groupId = group.id
        uiState.users.addAll(group.users)
        uiState.groupImage = group.image
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

    fun onGroupImageChange(uri: Uri?, context: Context) {
        if (uri != null) {
            uiState.groupImage = ImageUtil.uriToImageBitmap(uri, context)
        }
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
                users = uiState.users,
                imageBase64 = if (uiState.groupImage != null) ImageUtil.imageBitmapToBase64(uiState.groupImage!!) ?: "" else ""
            )
            if (newGroup != null) {
                _onComplete.send(newGroup)
            }
        }
    }

    fun updateGroup(context: Context) {
        if (!validateFields())
            return

        viewModelScope.launch {
            var groupImage = ""
            if (uiState.groupImage != null) {
                groupImage = ImageUtil.imageBitmapToBase64(uiState.groupImage!!) ?: ""
            }

            val updatedGroup = groupRepository.updateGroup(
                uiState.groupId,
                uiState.groupName,
                "",
                uiState.users,
                groupImage
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