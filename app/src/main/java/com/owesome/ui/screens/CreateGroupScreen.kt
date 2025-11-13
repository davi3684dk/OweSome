package com.owesome.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.owesome.Screen
import com.owesome.data.entities.User
import com.owesome.ui.viewmodels.CreateGroupViewModel
import com.owesome.ui.viewmodels.GroupViewModel
import com.owesome.ui.viewmodels.NavViewModel
import com.owesome.ui.viewmodels.AddUserViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateGroupScreen(viewModel: CreateGroupViewModel = koinInject(), navViewModel: NavViewModel = koinActivityViewModel(), groupViewModel: GroupViewModel = koinActivityViewModel(), navigation: NavController) {
    val openAddDialog = remember { mutableStateOf(false) }

    val scrollState = rememberLazyListState(0)

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        viewModel.onGroupImageChange(uri)
    }

    LaunchedEffect(Unit) {
        navViewModel.setTitle("Start a Group")
        viewModel.groupCreated.collect {
            groupViewModel.setGroup(it) //update group of shared viewmodel before navigating
            navigation.navigate(Screen.GroupDetails.createRoute(it.id)) {
                popUpTo(Screen.Groups.route)
            }
        }
    }

    LaunchedEffect(viewModel.users.size) {
        if (viewModel.users.isNotEmpty())
            scrollState.animateScrollToItem(viewModel.users.size)
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
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier.border(
                            width = 2.dp,
                            shape = CircleShape,
                            color = if (viewModel.imageError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                        ).clip(CircleShape),
                ) {
                    AsyncImage(
                        model = viewModel.groupImage,
                        contentDescription = "Selected Image",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Crop,
                        error = rememberVectorPainter(image = Icons.Default.Image),
                        fallback = rememberVectorPainter(image = Icons.Default.Image),
                        placeholder = rememberVectorPainter(Icons.Default.Image),
                        colorFilter =
                            if (viewModel.groupImage == null)
                                ColorFilter.tint(color = if (viewModel.imageError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline)
                            else null
                    )
                }

                OutlinedTextField(
                    value = viewModel.groupName,
                    onValueChange = { it ->
                        viewModel.onGroupNameChange(it)
                    },
                    label = { Text("Group Name") },
                    singleLine = true,
                    isError = viewModel.groupError,
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )
                Text(
                    text = "${viewModel.groupName.length} / ${viewModel.maxGroupNameLength}",
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

                        items(viewModel.users) { user ->
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
                                    IconButton(
                                        onClick = {
                                            viewModel.removeUser(user)
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
                                HorizontalDivider()
                            }
                        }
                    }
                }
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
        FloatingActionButton(
            onClick = {
                viewModel.createGroup()
            }
        ) {
            Text(
                text = "Create Group",
                modifier = Modifier.padding(20.dp)
            )
        }

        if (openAddDialog.value) {
            Dialog(
                onDismissRequest = { openAddDialog.value = false }
            ) {
                AddUserDialog(
                    onUserAdded = { user ->
                        viewModel.addUser(user)
                    }
                )
            }
        }
    }
}

@Composable
fun AddUserDialog(viewModel: AddUserViewModel = koinViewModel(), onUserAdded: (User) -> Unit) {
    val user = viewModel.foundUser

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            OutlinedTextField(
                value = viewModel.username,
                onValueChange = {
                    viewModel.onUsernameChange(it)
                },
                label = {Text("Username")},
                isError = viewModel.usernameError
            )
            if (viewModel.usernameError) {
                Text(
                    "User not found",
                    color = MaterialTheme.colorScheme.error
                )
            }
            if (viewModel.usernameSuccess) {
                Text(
                    "${viewModel.username} found!",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (user == null) {
                Button(
                    onClick = {
                        viewModel.searchUser()
                    },
                    modifier = Modifier.padding(top = 20.dp),
                    enabled = !viewModel.loading
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search User")
                    Text("Search User")
                }
            } else {
                Button(
                    onClick = {
                        onUserAdded(user)
                    },
                    modifier = Modifier.padding(top = 20.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Icon"
                    )
                    Text(text = "Add User")
                }
            }
        }
    }
}