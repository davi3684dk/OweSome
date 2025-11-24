package com.owesome.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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

    val scrollState = rememberLazyListState(0)

    val group by groupViewModel.currentGroup.collectAsState()

    LaunchedEffect(group) {
        navViewModel.setTitle(group.name)
        state.groupId = group.id.toInt()
        expenseViewModel.onComplete.collect {
            groupViewModel.setGroup(group.id)
            navigation.popBackStack()
        }
    }

    Column (
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(20.dp)
    ){
        Column {
            OutlinedTextField(
                value = state.expenseTitle,
                onValueChange = { state.expenseTitle = it },
                label = { Text("Title") },
                singleLine = true,
            )
        }
        OutlinedTextField(
            value = if (!state.customAmount) state.totalAmount else "",
            onValueChange = { state.totalAmount = it },
            label = { Text("Total Amount:") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            enabled = !state.customAmount
        )
        Button(
           onClick = { state.customAmount = !state.customAmount }
        ) {
            Text(if (state.customAmount) "Disable Custom Amount" else "Enable Custom Amount")
        }
        LazyColumn (
            reverseLayout = false,
            state = scrollState,
            modifier = Modifier.heightIn(0.dp,300.dp).fillMaxSize()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.extraSmall
                ),
        ) {
            items(group.users) { user ->
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(5.dp).fillMaxWidth(),
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(5f).padding(end = 5.dp)
                    ) {
                        Button(
                            onClick = { check(); },
                            modifier = Modifier.weight(5f)
                        ) {
                            Icon(Icons.Filled.AccountCircle, "profile picture")
                            Text(user.username)
                        }
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { check() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = if(checked) userAmount else "",
                        onValueChange = {
                            userAmount = it;
                            state.newUserMap.put(user.id, userAmount);
                            checked = !userAmount.isEmpty()
                                        },
                        label = { Text("Custom Amount:") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        enabled = state.customAmount,
                        modifier = Modifier.weight(4f)
                    )
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = {
                expenseViewModel.newCreateExpense()
            },
            icon = { Icon(Icons.Filled.Check, "Floating action button.") },
            text = { Text(text = "Confirm")},
        )
    }
}