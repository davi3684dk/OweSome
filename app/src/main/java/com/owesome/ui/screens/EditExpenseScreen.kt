package com.owesome.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.owesome.ui.viewmodels.GroupViewModel
import com.owesome.ui.viewmodels.NavViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun EditExpenseScreen (
    viewModel: GroupViewModel = koinActivityViewModel(),
    navViewModel: NavViewModel = koinActivityViewModel(),
    navigation: NavController
) {
    val group by viewModel.currentGroup.collectAsState()
    var split by rememberSaveable { mutableStateOf("Even") }

    LaunchedEffect(group) {
        navViewModel.setTitle(group.name)
    }

    Column (
        modifier = Modifier.fillMaxSize()
    ){
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Button(
                onClick = { split = "Even" }
            ) {
                Text("Even")
            }
            Button(
                onClick = { split = "Amount" }
            ) {
                Text("Amount")
            }
        }
        when (split) {
            "Even" -> {
                EvenExpenseDisplay()
            }

            "Amount" -> {
                AmountExpenseDisplay()
            }
        }
    }
}

@Composable
fun EvenExpenseDisplay () {
    Text(
        text = "even!"
    )
}

@Composable
fun AmountExpenseDisplay () {
    Text(
        text = "amount!"
    )
}