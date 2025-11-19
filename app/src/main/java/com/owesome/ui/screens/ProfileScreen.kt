package com.owesome.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PropaneTank
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.owesome.Screen
import com.owesome.ui.viewmodels.NavViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

// Enable users to update their profiles and manage notification settings.
// make as tests


val current_user = "Current active user"
@Composable
fun ProfileScreen(navigation: NavHostController) {


    Row (modifier = Modifier.fillMaxSize()){
    val navController = rememberNavController()

    var selectedDestination by rememberSaveable { mutableStateOf(Screen.Profile .route) }
    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        NavigationBarItem(
            selected = selectedDestination == Screen.Groups.route,
            onClick = {
                navController.navigate(route = Screen.Groups.route)
                selectedDestination = Screen.Groups.route
            },
            icon = {
                Icon(
                    Icons.Default.ManageAccounts,
                    contentDescription = Screen.Profile.route
                )
            },
            label = { Screen.Profile.label?.let { Text(it) } }
        )

        NavigationBarItem(
            selected = selectedDestination == Screen.Notifications.route,
            onClick = {
                navController.navigate(route = Screen.Notifications.route)
                selectedDestination = Screen.Notifications.route
            },
            icon = {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = Screen.Notifications.route
                )
            },
            label = { Screen.Notifications.label?.let { Text(it) } }
        )
    }
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
//            .verticalScroll(rememberScrollState())

 ){
        Text(text = "Account Management", fontSize = 24.sp)
        var viewModel: NavViewModel = koinActivityViewModel()
        viewModel.setTitle(current_user)


    OutlinedTextField(
//        supportingText = { Text("Username") },
        value = "",
        onValueChange = {},
        // this should be a R.string for multilanguage support
        label = { Text("Input Username") }
    )
        OutlinedTextField(
//            supportingText = { Text("email") },
            value = "",
            onValueChange = {},
            // this should be a R.string for multilanguage support
            label = { Text("Input Email") }
        )

        // TODO add region field for phone number in row
        OutlinedTextField(
//            supportingText = { Text("Phone Number") },
            value = "",
            onValueChange = {},
            // this should be a R.string for multilanguage support
            label = { Text("Input Phone Number") }
        )

        ExtendedFloatingActionButton(
            onClick = {
                navigation.navigate(Screen.CreateGroup.route)
            },
            icon = { Icon(Icons.Filled.Add, "Floating action button.") },
            text = { Text(text = "Update account details")},
            modifier = Modifier.padding(13.dp)
        )
        // not using horizontal divider breaks the centered alignment idk why
        HorizontalDivider()

        Text(text = "Password management", fontSize = 24.sp)

        OutlinedTextField(
//            supportingText = { Text("Old Password") },
            value = "",
            onValueChange = {},
            // this should be a R.string for multilanguage support
            label = { Text("Input old password") }
        )

        OutlinedTextField(
//            supportingText = { Text("New Password") },
            value = "",
            onValueChange = {},
            // this should be a R.string for multilanguage support
            label = { Text("Input new Password") }
        )

        OutlinedTextField(
//            supportingText = { Text("Confirm New Password") },
            value = "",
            onValueChange = {},
            // this should be a R.string for multilanguage support
            label = { Text("input the new Password again") }
        )
        ExtendedFloatingActionButton(
            onClick = {
                // TODO send call to update in database
//                navigation.navigate(Screen.CreateGroup.route)
            },
            icon = { Icon(Icons.Filled.Add, "Floating action button.") },
            text = { Text(text = "Update password")},
            modifier = Modifier.padding(13.dp)
        )
    }



}