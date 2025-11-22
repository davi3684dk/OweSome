package com.owesome.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.owesome.Screen
import com.owesome.data.entities.Expense
import com.owesome.ui.viewmodels.GroupViewModel
import com.owesome.ui.viewmodels.NavViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(viewModel: GroupViewModel = koinActivityViewModel(), navViewModel: NavViewModel = koinActivityViewModel(), navigation: NavController, groupId: String) {
    val group by viewModel.currentGroup.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val state = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        //Only fetch if group isn't already loaded
        if (groupId != group.id.toString())
            viewModel.setGroup(groupId)

        navViewModel.settingsPressed.collect {
            navigation.navigate(Screen.EditGroup.route)
        }
    }

    LaunchedEffect(group) {
        navViewModel.setTitle(group.name, Icons.Default.Settings)
    }

    PullToRefreshBox(
        state = state,
        isRefreshing = isLoading,
        onRefresh = {
            viewModel.setGroup(groupId)
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .dropShadow(
                        shape = RoundedCornerShape(0.dp),
                        shadow = Shadow(
                            radius = 6.dp,
                            spread = 6.dp,
                            color = Color(0x40000000),
                            offset = DpOffset(x = 0.dp, 4.dp)
                        )
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                ) {
                    ElevatedButton(
                        onClick = {

                        },
                    ) {
                        Text(
                            text = "Settle Up",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status:",
                            modifier = Modifier.padding(all = 4.dp),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "${group.status}",
                            modifier = Modifier.padding(all = 4.dp),
                            color = if (group.status < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 10.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxSize()
                        ) {
                            items(group.expenses) { expense ->
                                ExpenseBox(expense)
                            }

                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                )
                            }
                        }

                        ExtendedFloatingActionButton(
                            onClick = {
                                navigation.navigate(Screen.NewExpense.route)
                            },
                            icon = { Icon(Icons.Filled.Add, "Floating action button.") },
                            text = { Text(text = "New Expense")},
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseBox(expense: Expense) {
    val myExpense = expense.status > 0

    Row(
        horizontalArrangement = if (myExpense) Arrangement.End else Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        if (expense.status <= 0) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(32.dp)
            )
        }
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(100.dp)
                .padding(start = 10.dp, end = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (myExpense) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(

                ) {
                    Text(
                        expense.description,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (myExpense) "You paid: " else "You owe: ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${expense.status}kr.",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (myExpense) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.errorContainer
                    )
                }
            }
        }
        if (expense.status > 0) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(32.dp)
            )
        }
    }
}