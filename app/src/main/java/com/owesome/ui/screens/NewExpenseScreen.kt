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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.owesome.data.entities.Group
import com.owesome.ui.viewmodels.GroupViewModel
import com.owesome.ui.viewmodels.NavViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun NewExpenseScreen(
    viewModel: GroupViewModel = koinActivityViewModel(),
    navViewModel: NavViewModel = koinActivityViewModel(),
    navigation: NavController
) {
    val group by viewModel.currentGroup.collectAsState()
    var splitType by rememberSaveable { mutableStateOf("Even") }
    var message by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(group) {
        navViewModel.setTitle(group.name)
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
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") },
            maxLines = 10,
            modifier = Modifier.fillMaxWidth().padding(20.dp),
        )
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Button(
                onClick = { splitType = "Even" }
            ) {
                Text("Even")
            }
            Button(
                onClick = { splitType = "Amount" }
            ) {
                Text("Amount")
            }
        }
        when (splitType) {
            "Even" -> {
                EvenExpenseDisplay(group)
            }

            "Amount" -> {
                AmountExpenseDisplay(group)
            }
        }
        ExtendedFloatingActionButton(
            onClick = {

            },
            icon = { Icon(Icons.Filled.Check, "Floating action button.") },
            text = { Text(text = "Confirm")},
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
fun EvenExpenseDisplay (
    group: Group
) {
    var amount by rememberSaveable { mutableStateOf("") }
    var divideBy by rememberSaveable { mutableIntStateOf(1) }
    val selectedUsers = rememberSaveable {mutableStateListOf<Int>()}

    Text(
        text = "even! $divideBy plus"
    )
    Column {
        for (entry in selectedUsers) {
            Text("$entry")
        }
    }
    OutlinedTextField(
        value = amount,
        onValueChange = { amount = it },
        label = { Text("Amount") },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth().padding(20.dp),
    )
    LazyColumn (

    ) {
        item {
            for (user in group.users) {
                var checked by rememberSaveable {mutableStateOf(false)}
                fun check() {
                    checked = !checked
                    if (checked) {
                        divideBy++
                        selectedUsers.add(user.id)
                    } else {
                        divideBy--
                        selectedUsers.remove(user.id)
                    }
                }
                Row (
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = {
                            check()
                        },
                    ) {
                        Icon(Icons.Filled.AccountCircle, "profile picture")
                        Text(user.username)
                    }
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { check() }
                    )
                }
            }
        }
    }
}

@Composable
fun AmountExpenseDisplay (
    group: Group
) {
    Text(
        text = "amount!"
    )
}