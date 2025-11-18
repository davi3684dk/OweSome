package com.owesome.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.owesome.ui.viewmodels.NavViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

// Enable users to update their profiles and manage notification settings.
// make as tests


@Composable
fun ProfileScreen(navigation: NavHostController) {
    Column ( horizontalAlignment = Alignment.CenterHorizontally,
 ){
        Text(text = "Account Management", fontSize = 24.sp)
        var viewModel: NavViewModel = koinActivityViewModel()
        viewModel.setTitle("Profile")


    OutlinedTextField(
        supportingText = { Text("Username") },
        value = "",
        onValueChange = {},
        // this should be a R.string for multilanguage support
        label = { Text("Input Username") }
    )
        OutlinedTextField(
            supportingText = { Text("email") },
            value = "",
            onValueChange = {},
            // this should be a R.string for multilanguage support
            label = { Text("Input Email") }
        )

        OutlinedTextField(
            supportingText = { Text("Phone Number") },
            value = "",
            onValueChange = {},
            // this should be a R.string for multilanguage support
            label = { Text("Input Phone Number") }
        )
        HorizontalDivider()

        OutlinedTextField(
            supportingText = { Text("Phone Number") },
            value = "",
            onValueChange = {},
            // this should be a R.string for multilanguage support
            label = { Text("Input Phone Number") }
        )
        Text(text = "Password management", fontSize = 24.sp)

        OutlinedTextField(
            supportingText = { Text("Old Password") },
            value = "",
            onValueChange = {},
            // this should be a R.string for multilanguage support
            label = { Text("Input old password") }
        )

        OutlinedTextField(
            supportingText = { Text("New Password") },
            value = "",
            onValueChange = {},
            // this should be a R.string for multilanguage support
            label = { Text("input New Password") }
        )

        OutlinedTextField(
            supportingText = { Text("Confirm New Password") },
            value = "",
            onValueChange = {},
            // this should be a R.string for multilanguage support
            label = { Text("input the New Password again") }
        )
    }



}