package com.owesome.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.owesome.data.entities.Group
import com.owesome.data.entities.User
import com.owesome.ui.composables.AddUserDialog
import com.owesome.ui.composables.GroupEditorForm
import com.owesome.ui.viewmodels.GroupEditorUiState
import com.owesome.ui.viewmodels.GroupEditorViewModel
import com.owesome.ui.viewmodels.GroupViewModel
import com.owesome.ui.viewmodels.NavViewModel
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditGroupScreen(
    navigation: NavController,
    viewModel: GroupEditorViewModel = koinViewModel(),
    navViewModel: NavViewModel = koinActivityViewModel(),
    groupViewModel: GroupViewModel = koinActivityViewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        navViewModel.setTitle(groupViewModel.currentGroup.value.name)
        viewModel.setGroup(groupViewModel.currentGroup.value)

        viewModel.onComplete.collect {
            groupViewModel.setGroup(it.id) //update group of shared viewmodel before navigating
            groupViewModel.getAllGroups() //update group list
            navigation.popBackStack()
        }
    }

    GroupEditorForm(
        uiState,
        onNameChange = {
            viewModel.onGroupNameChange(it)
        },
        onImageChange = {
            viewModel.onGroupImageChange(it, context)
        },
        onUserAdded = {
            viewModel.addUser(it)
        },
        onUserRemoved = {
            viewModel.removeUser(it)
        },
        onSubmit = {
            viewModel.updateGroup(context)
        },
        "Save"
    )
}

