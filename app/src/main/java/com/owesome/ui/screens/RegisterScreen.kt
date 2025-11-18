package com.owesome.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.owesome.R
import com.owesome.Screen
import com.owesome.ui.viewmodels.RegisterViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navigation: NavController,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val state = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.onComplete.collect {
            if (it) {
                navigation.navigate(Screen.Login.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(44.dp))

        OutlinedTextField(
            value = state.username,
            onValueChange = { state.username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (state.usernameError != null) {
            Text(
                text = state.usernameError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier =  Modifier.height(16.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { state.email = it },
            label = { Text("Email") },
            isError = state.emailError != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (state.emailError != null) {
            Text(
                text = state.emailError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier =  Modifier.height(16.dp))

        // TODO: Dropdown with country code picker with flag + validation for phonenumber/countrycode
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.countryCode,
                onValueChange = { state.countryCode = it },
                modifier = Modifier.width(100.dp),
                singleLine = true,
                leadingIcon = {
                    /*Icon(
                        painter = painterResource(id = R.drawable.flag_denmark),
                        contentDescription = "Country Flag"
                    )*/
                },
                label = { Text("Code") }
            )

            Spacer(modifier = Modifier.width(8.dp))


            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = { state.phoneNumber = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

        }

        Spacer(modifier =  Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { state.password = it },
            label = { Text("Password") },
            singleLine = true,
            isError = state.passwordError != null,
            visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (state.passwordVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                val description = if (state.passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { state.passwordVisible = !state.passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (state.passwordError != null) {
            Text(
                text = state.passwordError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = { state.confirmPassword = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            isError = state.confirmPasswordError != null,
            visualTransformation = if (state.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val confirmImage = if (state.confirmPasswordVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                val confirmDescription = if (state.confirmPasswordVisible) "Hide password" else "Show password"

                IconButton(onClick = { state.confirmPasswordVisible = !state.confirmPasswordVisible }) {
                    Icon(imageVector = confirmImage, contentDescription = confirmDescription)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (state.confirmPasswordError != null) {
            Text(
                text = state.confirmPasswordError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier =  Modifier.height(26.dp))

        state.errorMsg?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        ClickableText(
            text = AnnotatedString(
                "Already have an account? Login here.",
                spanStyle = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                )
            ),
            onClick = {
                navigation.navigate(Screen.Login.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }
        )

        Spacer(modifier =  Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.registerUser()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

    }
}