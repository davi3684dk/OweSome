package com.owesome.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.owesome.data.entities.User
import com.owesome.ui.viewmodels.AddUserViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddUserDialog(viewModel: AddUserViewModel = koinViewModel(), onUserAdded: (User) -> Unit) {

    LaunchedEffect(Unit) {
        viewModel.onComplete.collect {
            onUserAdded(it)
        }
    }

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

            Button(
                onClick = {
                    viewModel.searchUser()
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