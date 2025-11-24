package com.owesome.ui.screens

import ads_mobile_sdk.my
import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.owesome.Screen
import com.owesome.data.entities.Expense
import com.owesome.data.entities.Settlement
import com.owesome.data.entities.User
import com.owesome.ui.composables.SliderButton
import com.owesome.ui.viewmodels.GroupViewModel
import com.owesome.ui.viewmodels.NavViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.core.qualifier._q
import java.time.OffsetDateTime

sealed interface GroupMessage {
    val createdAt: OffsetDateTime
}

data class ExpenseMessage(
    var sender: User,
    var expense: Expense,
    override val createdAt: OffsetDateTime
) : GroupMessage

data class SettlementMessage(
    var settlement: Settlement,
    override val createdAt: OffsetDateTime
) : GroupMessage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(viewModel: GroupViewModel = koinActivityViewModel(), navViewModel: NavViewModel = koinActivityViewModel(), navigation: NavController, groupId: String) {
    val group by viewModel.currentGroup.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val state = rememberPullToRefreshState()

    val lazyState = rememberLazyListState()
    val currentUser by viewModel.currentUser.collectAsState()

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

    LaunchedEffect(group.expenses.size) {
        if (group.expenses.isNotEmpty())
            lazyState.scrollToItem(group.expenses.size - 1)
    }

    val unpaidSettlements = group.settlements.filter {
        it.payer.id == (currentUser?.id ?: -1) && it.paidAt == null
    }

    if (unpaidSettlements.isNotEmpty()) {

        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Group ${group.name} has been settled!"
                )

                Text(
                    text = "${unpaidSettlements.size} Payment(s) remaining",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 120.dp)
                )

                Text(
                    text = "You still owe",
                    modifier = Modifier.padding(top = 100.dp)
                )

                Text(
                    text = String.format("%.2f", unpaidSettlements[0].amount) + " kr.",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "To"
                )

                Text(
                    text = unpaidSettlements[0].receiver.username,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            SliderButton(
                text = "Confirm Payment",
                size = 48f,
                loading = isLoading,
                onComplete = {
                    viewModel.confirmSettlement(unpaidSettlements[0])
                }
            )
        }


    } else {
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
                        if (currentUser?.id == group.owner.id) {
                            ElevatedButton(
                                onClick = {
                                    viewModel.settleGroup()
                                },
                            ) {
                                Text(
                                    text = "Settle Up",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
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
                                text = "${String.format("%.2f", group.status)}",
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
                                state = lazyState,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxSize()
                            ) {
                                val messages =
                                    group.expenses.map { ExpenseMessage(it.paidBy, it, it.createdAt) } +
                                    group.settlements.map { SettlementMessage(it, it.createdAt) }

                                items(messages.sortedBy { it.createdAt }) { message ->
                                    when (message) {
                                        is ExpenseMessage -> ExpenseBox(message.expense, currentUser?.id ?: -1)
                                        is SettlementMessage -> SettlementBox(message.settlement, currentUser?.id ?: -1)
                                    }

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
}

@Composable
fun ExpenseBox(expense: Expense, currentUserId: Int) {
    val myExpense = expense.paidBy.id == currentUserId

    Row(
        horizontalArrangement = if (myExpense) Arrangement.End else Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .alpha(if (expense.settled) 0.5f else 1f)
    ) {
        if (!myExpense) {
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
            colors = CardDefaults.cardColors(containerColor = if (myExpense) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (!myExpense) {
                    Row(

                    ) {
                        Text(
                            expense.paidBy.username,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

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
                        text = if (myExpense && expense.status > 0) "Owed: " else "You owe: ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${String.format("%.2f", expense.status)}kr.",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (myExpense) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.errorContainer
                    )
                }
            }
        }
        if (myExpense) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(32.dp)
            )
        }
    }
}

@Composable
fun SettlementBox(settlement: Settlement, currentUserId: Int) {
    fun getUserDisplayName(user: User): String {
        if (user.id == currentUserId)
            return "You"
        else
            return user.username
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (settlement.paidAt != null) {
            Icon(
                Icons.Default.CheckCircle,
                "checkmark",
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "${getUserDisplayName(settlement.payer)} paid ${getUserDisplayName(settlement.receiver)} ${
                    String.format(
                        "%.2f",
                        settlement.amount
                    )
                } kr.",
                modifier = Modifier.padding(5.dp),
                color = MaterialTheme.colorScheme.primary
            )

        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "${getUserDisplayName(settlement.payer)} still owes ${getUserDisplayName(settlement.receiver)} ${
                        String.format(
                            "%.2f",
                            settlement.amount
                        )
                    } kr.",
                    modifier = Modifier.padding(5.dp),
                    color = MaterialTheme.colorScheme.error
                )

                if (settlement.receiver.id == currentUserId) {

                    ElevatedButton(
                        onClick = {

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text(
                            text = "Send Reminder"
                        )
                    }

                }
            }
        }
    }
}