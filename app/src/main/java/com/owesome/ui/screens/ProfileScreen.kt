package com.owesome.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.owesome.Screen
import com.owesome.ui.viewmodels.NavViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

// Enable users to update their profiles and manage notification settings.
val current_user = "Current active user"
@Composable
fun ProfileScreen(navigation: NavHostController) {

    // Use a Column to stack the navigation bar and the content vertically.
    Column (modifier = Modifier.fillMaxSize()){
        var selectedDestination by rememberSaveable { mutableStateOf(Screen.Profile.route) }

        // This NavigationBar is now at the top of the screen.
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            windowInsets = NavigationBarDefaults.windowInsets
        ) {
            NavigationBarItem(
                selected = selectedDestination == Screen.Profile.route,
                onClick = { selectedDestination = Screen.Profile.route },
                icon = {
                    Icon(
                        Icons.Default.ManageAccounts,
                        contentDescription = "Account"
                    )
                },
                label = { Text("Account") }
            )

            NavigationBarItem(
                selected = selectedDestination == Screen.Notifications.route,
                onClick = { selectedDestination = Screen.Notifications.route },
                icon = {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = Screen.Notifications.label
                    )
                },
                label = { Screen.Notifications.label?.let { Text(it) } }
            )
        }

        // Show content based on the selected destination.
        if (selectedDestination == Screen.Profile.route) {
            AccountManagementContent(navigation)
        } else {
            NotificationSettingsContent()
        }
    }
}

@Composable
fun AccountManagementContent(navigation: NavHostController) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var oldPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmNewPassword by rememberSaveable { mutableStateOf("") }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Text(text = "Account Management", fontSize = 24.sp)
        val viewModel: NavViewModel = koinActivityViewModel()
        viewModel.setTitle(current_user)

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Input Username") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Input Email") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        // TODO add region field for phone number in a row
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Input Phone Number") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        ExtendedFloatingActionButton(
            onClick = {
                navigation.navigate(Screen.CreateGroup.route)
            },
            icon = { Icon(Icons.Filled.Add, "Floating action button.") },
            text = { Text(text = "Update account details")},
            modifier = Modifier.padding(13.dp)
        )

        Text(text = "Password management", fontSize = 24.sp)

        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = { Text("Input old password") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Input new Password") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = confirmNewPassword,
            onValueChange = { confirmNewPassword = it },
            label = { Text("input the new Password again") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        ExtendedFloatingActionButton(
            onClick = {
                // TODO send call to update in database
            },
            icon = { Icon(Icons.Filled.Add, "Floating action button.") },
            text = { Text(text = "Update password")},
            modifier = Modifier.padding(13.dp)
        )
    }
}

@Composable
fun NotificationSettingsContent() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Notification Settings", fontSize = 24.sp)
        // Add your notification settings UI here
    }
}
