package com.owesome.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.owesome.data.auth.AuthManager
import com.owesome.ui.viewmodels.NavViewModel
import org.koin.compose.currentKoinScope
import org.koin.compose.getKoin
import org.koin.compose.koinInject
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
    var phonePrefix by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var oldPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmNewPassword by rememberSaveable { mutableStateOf("") }

    val authManager = koinInject<AuthManager>()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        val viewModel: NavViewModel = koinActivityViewModel()
        viewModel.setTitle(current_user)

        Spacer(modifier =  Modifier.height(16.dp))
        Text(text = "Account Management", fontSize = 24.sp)
        Spacer(modifier =  Modifier.height(4.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("New Username") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("New Email") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Row (
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            OutlinedTextField(
                value = phonePrefix,
                onValueChange = { phonePrefix = it },
                label = { Text("Prefix") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.weight(3f).fillMaxHeight()
            )
        }

        ExtendedFloatingActionButton(
            onClick = {
                navigation.navigate(Screen.CreateGroup.route)
            },
            icon = { Icon(Icons.Filled.Add, "Floating action button.") },
            text = { Text(text = "Update Account Details")},
            modifier = Modifier.padding(13.dp)
        )

        Spacer(modifier =  Modifier.height(16.dp))
        Text(text = "Password management", fontSize = 24.sp)
        Spacer(modifier =  Modifier.height(4.dp))

        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = { Text("Old Password") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = confirmNewPassword,
            onValueChange = { confirmNewPassword = it },
            label = { Text("Confirm New Password ") },
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

        TextButton(
            onClick = {
                authManager.clearTokens()
                navigation.navigate(Screen.Groups.route) {
                    popUpTo(Screen.Groups.route)
                }
            },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Logout,
                "Logout Icon",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(end = 5.dp)
            )
            Text(
                "Logout",
                color = MaterialTheme.colorScheme.error
            )
        }
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
