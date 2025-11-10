package com.owesome.ui.screens

import android.R
import android.graphics.drawable.Icon
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.owesome.Screen
import com.owesome.data.entities.User
import com.owesome.ui.viewmodels.GroupViewModel
import com.owesome.ui.viewmodels.NavViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun CreateGroupScreen(navViewModel: NavViewModel = koinActivityViewModel(), groupViewModel: GroupViewModel = koinActivityViewModel(), navigation: NavController) {

    var groupName by rememberSaveable { mutableStateOf("") }
    var groupImage by rememberSaveable { mutableStateOf<Uri?>(null) }
    var users by rememberSaveable { mutableStateOf(listOf<List<User>>()) }
    val maxGroupNameLength = 30

    val context = LocalContext.current

    var groupError by remember { mutableStateOf(false) }
    var imageError by remember { mutableStateOf(false) }

    val openAddDialog = remember { mutableStateOf(false) }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            groupImage = uri
            imageError = false
            /*
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                groupImage = Base64.encodeToString(bytes, Base64.DEFAULT)
            }*/
        }
    }

    LaunchedEffect(Unit) {
        navViewModel.setTitle("Start a Group")
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
                            color = if (imageError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                        ).clip(CircleShape),
                ) {
                    AsyncImage(
                        model = groupImage,
                        contentDescription = "Selected Image",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Crop,
                        error = rememberVectorPainter(image = Icons.Default.Image),
                        fallback = rememberVectorPainter(image = Icons.Default.Image),
                        placeholder = rememberVectorPainter(Icons.Default.Image),
                        colorFilter =
                            if (groupImage == null)
                                ColorFilter.tint(color = if (imageError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline)
                            else null
                    )
                }

                OutlinedTextField(
                    value = groupName,
                    onValueChange = { it ->
                        if (it.length <= maxGroupNameLength) {
                            groupName = it
                            groupError = false
                        }
                    },
                    label = { Text("Group Name") },
                    singleLine = true,
                    isError = groupError,
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )
                Text(
                    text = "${groupName.length} / $maxGroupNameLength",
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Profile Icon",
                            modifier = Modifier.padding(end = 10.dp).size(32.dp)
                        )
                        Text("You")
                    }
                }
            }
            Button(
                onClick = {
                    openAddDialog.value = true
                },
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
        FloatingActionButton(
            onClick = {
                if (groupName.isEmpty()) {
                    groupError = true
                }

                if (groupImage == null) {
                    imageError = true
                }

                if (groupError || imageError)
                    return@FloatingActionButton

                //TODO groupViewModel.createGroup()

                //TODO navigation.navigate()
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
                    onUserAdded = {

                    }
                )
            }
        }
    }
}

@Composable
fun AddUserDialog(onUserAdded: (User) -> Unit) {
    var username by remember { mutableStateOf("") }

    var usernameError by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = false
                },
                label = {Text("Username")},
                isError = usernameError
            )
            Button(
                onClick = {
                    if (username.isEmpty()) {
                        usernameError = true
                        return@Button
                    }
                    //TODO("Need to search for user by username")
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