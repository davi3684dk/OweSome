package com.owesome

import android.Manifest
import android.content.Context
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.owesome.data.api.AuthApiService
import com.owesome.data.api.LoginRequest
import com.owesome.data.api.LoginResponse
import com.owesome.data.api.RegisterRequest
import com.owesome.data.auth.AuthManager
import com.owesome.data.entities.User
import com.owesome.data.entities.UserCreate
import com.owesome.data.repository.Result
import com.owesome.data.repository.UserRepository
import com.owesome.data.repository.NotificationRepository
import com.owesome.di.appModule
import com.owesome.ui.screens.CreateGroupScreen
import com.owesome.notifications.NotificationFacade
import com.owesome.ui.screens.EditGroupScreen
import com.owesome.ui.screens.GroupScreen
import com.owesome.ui.screens.GroupsScreen
import com.owesome.ui.screens.LoginScreen
import com.owesome.ui.screens.NewExpenseScreen
import com.owesome.ui.screens.RegisterScreen
import com.owesome.ui.screens.ProfileScreen
import com.owesome.ui.screens.SplashScreen
import com.owesome.ui.theme.OweSomeTheme
import com.owesome.ui.viewmodels.NavViewModel
import com.owesome.util.AlertManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.core.context.startKoin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log
import kotlin.system.exitProcess


class MainActivity : ComponentActivity() {

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your
            // app.
        } else {
            // Explain to the user that the feature is unavailable because the
            // feature requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    }

    var pollingJob: Job? = null

    private val notificationFacade: NotificationFacade by inject()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin{
            androidLogger()
            androidContext(this@MainActivity)
            modules(appModule)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val store = viewModelStore

        enableEdgeToEdge()
        setContent {
            val userRepo: UserRepository = koinInject()
            val authManager: AuthManager = koinInject()
            val alertManager: AlertManager = koinInject()

            var connected by rememberSaveable { mutableStateOf(false) }

            val currentUser by authManager.currentUser.collectAsState()

            val context = LocalContext.current

            val alert by alertManager.alert.collectAsState()

            LaunchedEffect(Unit) {
                val user = userRepo.getUser()
                when (user) {
                    is Result.Success -> {
                        if (user.value != null)
                            authManager.setCurrentUser(user.value)

                        connected = true
                    }

                    is Result.Error -> {
                        Toast.makeText(context, user.message, Toast.LENGTH_LONG).show()
                    }

                    is Result.ConnectionError -> {
                        alertManager.showAlert(
                            title = "Error",
                            message = "Could not connect to server.. Please ensure you have an internet connection or try again later",
                            onDismiss = {
                                exitProcess(-1)
                            }
                        )
                    }
                }
            }
            OweSomeTheme(
                darkTheme = true,
                dynamicColor = false
            ) {
                alert?.let { a ->
                    AlertDialog(
                        onDismissRequest = {
                            alertManager.hideAlert()

                            if (a.onDismiss != null)
                                a.onDismiss()
                        },
                        dismissButton = {
                            a.onDismiss?.let {
                                Button(
                                    onClick = {
                                        alertManager.hideAlert()
                                        it()
                                    }
                                ) {
                                    Text(a.dismissText ?: "")
                                }
                            }
                        },
                        confirmButton = {
                            a.onConfirm?.let {
                                Button(
                                    onClick = {
                                        alertManager.hideAlert()
                                        it()
                                    }
                                ) {
                                    Text(a.confirmText ?: "")
                                }
                            }
                        },
                        text = {
                            Text(a.message)
                        },
                        title = {
                            Text(a.title)
                        }
                    )
                }

                if (connected) {
                    if (currentUser != null) {
                        OweSome()
                    } else {
                        store.clear()
                        AuthNavGraph()
                    }
                } else {
                    SplashScreen()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        pollingJob = lifecycleScope.launch {
            while (isActive)
                notificationFacade.listen()
        }
    }

    override fun onStop() {
        super.onStop()
        pollingJob?.cancel()
    }
}

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OweSome(
    viewModel: NavViewModel = koinActivityViewModel()
) {
    val navController = rememberNavController()
    val headerTitle by viewModel.title.collectAsState()
    val currentStack = navController.currentBackStack.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(headerTitle) },
                navigationIcon = {
                    if (currentStack.value.size > 2) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    }
                },
                actions = {
                    viewModel.settingsIcon?.let {
                        IconButton(onClick = {
                            viewModel.settingsPressed()
                        }) {
                            Icon(
                                imageVector = viewModel.settingsIcon!!,
                                contentDescription = "Localized description"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                NavigationBarItem(
                    selected = currentStack.value.lastOrNull()?.destination?.route == Screen.Groups.route,
                    onClick = {
                        navController.navigate(route = Screen.Groups.route) {
                            popUpTo(Screen.Groups.route) {
                                inclusive = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Groups,
                            contentDescription = Screen.Groups.route
                        )
                    },
                    label = { Screen.Groups.label?.let { Text(it) } }
                )
                NavigationBarItem(
                    selected = currentStack.value.lastOrNull()?.destination?.route == Screen.Profile.route,
                    onClick = {
                        navController.navigate(route = Screen.Profile.route)
                    },
                    icon = {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = Screen.Profile.route
                        )
                    },
                    label = { Screen.Profile.label?.let { Text(it) } }
                )
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Groups.route
            ) {
                composable(Screen.Groups.route) {
                    GroupsScreen(navigation = navController)
                }

                composable(
                    Screen.GroupDetails.route
                ) { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getString("groupId")
                    groupId?.let { id ->
                        GroupScreen(groupId = id, navigation = navController)
                    }
                }

                composable(Screen.CreateGroup.route) {
                    CreateGroupScreen(navigation = navController)
                }

                composable(Screen.EditGroup.route) {
                    EditGroupScreen(navigation = navController)
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(navigation = navController)
                }

                composable(Screen.NewExpense.route) {
                    NewExpenseScreen(navigation = navController)
                }
            }
        }
    }
}


@Composable
fun AuthNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Login.route
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        navController = navController,
                    ) {}
                }

                composable(Screen.Register.route) {
                    RegisterScreen(navigation = navController)
                }
            }
        }
    }
}

sealed class Screen(
    val route: String,
    val label: String?,
) {
    object Groups : Screen("groups", "Groups")
    object Profile : Screen("profile", "Profile")
    object Settings : Screen("settings", "Settings")
    object Notifications : Screen("notifications", "Notifications")
    object CreateGroup : Screen("createGroup", "Create Group")
    object EditGroup : Screen("editGroup", "Edit Group")
    object Login : Screen("login", "Login")
    object Register: Screen("register", "Register")
    object NewExpense: Screen("newExpense", "New Expense")

    object GroupDetails : Screen("groupDetails/{groupId}", null) {
        fun createRoute(groupId: String) = "groupDetails/$groupId"
    }
}