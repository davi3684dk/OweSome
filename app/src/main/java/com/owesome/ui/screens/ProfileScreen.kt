package com.owesome.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController

// Enable users to update their profiles and manage notification settings.
// make as tests
@Composable
fun ProfileScreen(navigation: NavHostController) {
    Column ( horizontalAlignment = Alignment.CenterHorizontally,
 ){

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
    }




}