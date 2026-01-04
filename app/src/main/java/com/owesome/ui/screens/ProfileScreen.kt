package com.owesome.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.owesome.data.entities.UserCreate
import com.owesome.data.repository.UserRepositoryImpl
import com.owesome.ui.viewmodels.ProfileViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel


// Enable users to update their profiles and manage notification settings.
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
fun AccountManagementContent(
    navigation: NavHostController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state = viewModel.uiState
    val context = LocalContext.current

    val authManager = koinInject<AuthManager>()
    val userRepo = koinInject<UserRepositoryImpl>()

    LaunchedEffect(Unit) {
        // Provide feedback on successful update
        viewModel.onUpdateDetails.collect {
            if (it) {
                Toast.makeText(context, "Updated account successfully", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.onUpdatePassword.collect {
            if (it) {
                Toast.makeText(context, "Updated password successfully", Toast.LENGTH_LONG).show()
            }
        }
    }

    // we are not allowed to catch nullpointer exceptions from composables,
    // so it is unhandled
    var current_user = authManager.currentUser.collectAsState().value?.username

    Column (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        val navModel: NavViewModel = koinActivityViewModel()
        navModel.setTitle(current_user)

        Spacer(modifier =  Modifier.height(16.dp))
        Text(text = "Account Management", fontSize = 24.sp)
        Spacer(modifier =  Modifier.height(4.dp))

        OutlinedTextField(
            value = state.username,
            onValueChange = {
                state.username = it
                state.usernameError = viewModel.validateUsername(it) },
            label = { Text("New Username") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        state.usernameError?.let {
            Text(
                text = state.usernameError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier =  Modifier.height(16.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = {
                state.email = it
                state.emailError = viewModel.validateEmail(it) },
            label = { Text("New Email") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        state.emailError?.let {
            Text(
                text = state.emailError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier =  Modifier.height(16.dp))

        Row (
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            OutlinedTextField(
                value = state.phonePrefix,
                onValueChange = { state.phonePrefix = it },
                label = { Text("Prefix") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = { state.phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.weight(3f).fillMaxHeight()
            )
        }

        ExtendedFloatingActionButton(
            onClick = {
                // double check relevant fields
                // build a request based on updated fields and call the api
                viewModel.changeAccontDetails()
            },
            icon = { Icon(Icons.Filled.Add, "Floating action button.") },
            text = { Text(text = "Update Account Details")},
            modifier = Modifier.padding(13.dp)
        )

        Spacer(modifier =  Modifier.height(16.dp))
        Text(text = "Password management", fontSize = 24.sp)
        Spacer(modifier =  Modifier.height(4.dp))

        OutlinedTextField(
            value = state.oldPassword,
            onValueChange = { state.oldPassword = it },
            label = { Text("Old Password") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Spacer(modifier =  Modifier.height(16.dp))

        OutlinedTextField(
            value = state.newPassword,
            onValueChange = {
                state.newPassword = it
                state.newPasswordError = viewModel.validateNewPassword(it) },
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        state.newPasswordError?.let {
            Text(
                text = state.newPasswordError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier =  Modifier.height(16.dp))

        OutlinedTextField(
            value = state.confirmNewPassword,
            onValueChange = {
                state.confirmNewPassword = it
                state.confirmNewPasswordError = viewModel.validateConfirmNewPassword(state.newPassword, it)},
            label = { Text("Confirm New Password ") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        ExtendedFloatingActionButton(
            onClick = {
                viewModel.changePassword()
            },
            icon = { Icon(Icons.Filled.Add, "Floating action button.") },
            text = { Text(text = "Update password")},
            modifier = Modifier.padding(13.dp)
        )

        Spacer(modifier =  Modifier.height(16.dp))

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
