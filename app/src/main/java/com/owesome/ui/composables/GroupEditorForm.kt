package com.owesome.ui.composables

import ads_mobile_sdk.ui
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.owesome.data.entities.User
import com.owesome.ui.viewmodels.GroupEditorUiState

@Composable
fun GroupEditorForm(
    uiState: GroupEditorUiState,
    onNameChange: (String) -> Unit,
    onImageChange: (Uri?) -> Unit,
    onUserAdded: (User) -> Unit,
    onUserRemoved: (User) -> Unit,
    onSubmit: () -> Unit,
    submitButtonText: String?
) {
    val openAddDialog = remember { mutableStateOf(false) }

    val scrollState = rememberLazyListState(0)

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        onImageChange(uri)
    }

    LaunchedEffect(uiState.users.size) {
        if (uiState.users.isNotEmpty())
            scrollState.animateScrollToItem(uiState.users.size)
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(vertical = 20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
            ) {
                Surface(
                    onClick = {
                        if (uiState.isOwner)
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier.border(
                        width = 2.dp,
                        shape = CircleShape,
                        color = if (uiState.imageError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                    ).clip(CircleShape),
                ) {
                    AsyncImage(
                        model = uiState.groupImage?.asAndroidBitmap(),
                        contentDescription = "Selected Image",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Crop,
                        error = rememberVectorPainter(image = Icons.Default.Image),
                        fallback = rememberVectorPainter(image = Icons.Default.Image),
                        placeholder = rememberVectorPainter(Icons.Default.Image),
                        colorFilter =
                            if (uiState.groupImage == null)
                                ColorFilter.tint(color = if (uiState.imageError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline)
                            else null
                    )
                }

                OutlinedTextField(
                    value = uiState.groupName,
                    onValueChange = { it ->
                        onNameChange(it)
                    },
                    enabled = uiState.isOwner,
                    label = { Text("Group Name") },
                    singleLine = true,
                    isError = uiState.nameError,
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )
                Text(
                    text = "${uiState.groupName.length} / ${uiState.maxGroupNameLength}",
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.fillMaxWidth().padding(end = 16.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 20.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Participants:",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    LazyColumn(
                        reverseLayout = false,
                        state = scrollState,
                        modifier = Modifier.heightIn(0.dp, 140.dp).fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = MaterialTheme.shapes.extraSmall
                            ),

                        ) {
                        item {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(5.dp).fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Default.AccountCircle,
                                        contentDescription = "Profile Icon",
                                        modifier = Modifier.padding(end = 10.dp).size(32.dp)
                                    )
                                    Text("You")
                                }
                                HorizontalDivider()
                            }
                        }

                        items(uiState.users) { user ->
                            Column{
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.padding(5.dp).fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.AccountCircle,
                                            contentDescription = "Profile Icon",
                                            modifier = Modifier.padding(end = 10.dp).size(32.dp)
                                        )
                                        Text(user.username)
                                    }
                                    if (uiState.isOwner) {
                                        IconButton(
                                            onClick = {
                                                onUserRemoved(user)
                                            },
                                            modifier = Modifier.padding(end = 10.dp).size(32.dp),
                                        ) {
                                            Icon(
                                                Icons.Default.RemoveCircle,
                                                contentDescription = "Remove Icon",
                                                tint = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider()
                            }
                        }
                    }
                }
                if (uiState.isOwner) {
                    Button(
                        onClick = {
                            openAddDialog.value = true
                        },
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Default.AddCircleOutline,
                                contentDescription = "Add participant",
                                modifier = Modifier.size(32.dp).padding(end = 10.dp),
                            )
                            Text(
                                "Add participant",
                            )
                        }
                    }
                }
            }
        }
        submitButtonText?.let {
            FloatingActionButton(
                onClick = {
                    onSubmit()
                }
            ) {
                Text(
                    text = submitButtonText,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }

        if (openAddDialog.value) {
            Dialog(
                onDismissRequest = { openAddDialog.value = false }
            ) {
                AddUserDialog(
                    onUserAdded = { user ->
                        onUserAdded(user)
                        openAddDialog.value = false
                    }
                )
            }
        }
    }
}