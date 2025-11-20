package com.owesome.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.owesome.data.entities.Group
import com.owesome.ui.viewmodels.ExpenseViewModel
import com.owesome.ui.viewmodels.GroupViewModel
import com.owesome.ui.viewmodels.NavViewModel
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewExpenseScreen(
    groupViewModel: GroupViewModel = koinActivityViewModel(),
    expenseViewModel: ExpenseViewModel = koinViewModel(),
    navViewModel: NavViewModel = koinActivityViewModel(),
    navigation: NavController
) {
    val state = expenseViewModel.uiState
    
    val group by groupViewModel.currentGroup.collectAsState()

    LaunchedEffect(group) {
        navViewModel.setTitle(group.name)
        state.groupId = group.id.toInt()
        expenseViewModel.onComplete.collect { navigation.popBackStack() }
    }

    Column (
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(20.dp)
    ){
        Text(
            text = "New Expense",
            style = MaterialTheme.typography.bodyLarge,
        )
        OutlinedTextField(
            value = state.expenseTitle,
            onValueChange = { state.expenseTitle = it },
            label = { Text("Title") },
            maxLines = 10,
        )
        OutlinedTextField(
            value = state.totalAmount,
            onValueChange = { state.totalAmount = it },
            label = { Text("Total Amount:") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        )
        Button(
           onClick = { state.customAmount = !state.customAmount }
        ) {
            Text(if (state.customAmount) "Disable custom amount" else "Enable custom amount")
        }
        LazyColumn (

        ) {
            item {
                for (user in group.users) {
                    var checked by rememberSaveable {mutableStateOf(false)}
                    var userAmount by rememberSaveable { mutableStateOf("") }
                    fun check() {
                        checked = !checked
                        if (checked) {
                            state.selectedUsers.add(user.id)
                        } else {
                            state.selectedUsers.remove(user.id)
                        }
                    }
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Button(
                            onClick = { check(); },
                        ) {
                            Icon(Icons.Filled.AccountCircle, "profile picture")
                            Text(user.username)
                        }
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { check() }
                        )
                        OutlinedTextField(
                            value = if(checked) userAmount else "",
                            onValueChange = {
                                userAmount = it;
                                expenseViewModel.mapUserAmount(user.id, userAmount);
                                checked = !userAmount.isEmpty()
                                            },
                            label = { Text("Custom Amount:") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            enabled = state.customAmount
                        )
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = {
                expenseViewModel.createExpense()
            },
            icon = { Icon(Icons.Filled.Check, "Floating action button.") },
            text = { Text(text = "Confirm")},
            modifier = Modifier.padding(20.dp)
        )
    }
}