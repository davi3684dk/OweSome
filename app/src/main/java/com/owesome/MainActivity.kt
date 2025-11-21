package com.owesome

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.owesome.di.appModule
import com.owesome.ui.screens.CreateGroupScreen
import com.owesome.notifications.NotificationFacade
import com.owesome.ui.screens.EditGroupScreen
import com.owesome.ui.screens.GroupScreen
import com.owesome.ui.screens.GroupsScreen
import com.owesome.ui.screens.LoginScreen
import com.owesome.ui.screens.NewExpenseScreen
import com.owesome.ui.screens.RegisterScreen
import com.owesome.ui.theme.OweSomeTheme
import com.owesome.ui.viewmodels.NavViewModel
import kotlinx.coroutines.delay
import okhttp3.ResponseBody
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.core.context.startKoin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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

        enableEdgeToEdge()
        setContent {
            OweSome()
        }
    }
}

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OweSome(viewModel: NavViewModel = koinActivityViewModel(), authManager: AuthManager = koinInject()) {
    val navController = rememberNavController()
    var selectedDestination by rememberSaveable { mutableStateOf(Screen.Groups.route) }

    val headerTitle by viewModel.title.collectAsState()

    val currentStack = navController.currentBackStack.collectAsState()

    LaunchedEffect(Unit) {
        authManager.loginRequired.collect {
            //TODO navController.navigate()
        }
    }

    viewModel.setTitle("Test")
    val notificationFacade = koinInject<NotificationFacade>()
    OweSomeTheme(
        darkTheme = true,
        dynamicColor = false
    ) {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            headerTitle
                        ) },
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
                    /*modifier = Modifier.dropShadow(
                       shape = RoundedCornerShape(0.dp),
                       shadow = Shadow(
                           radius = 6.dp,
                           spread = 6.dp,
                           color = Color(0x40000000),
                           offset = DpOffset(x = 0.dp, 4.dp)
                       )
                   )*/) },
            bottomBar = {
                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                    NavigationBarItem(
                        selected = selectedDestination == Screen.Groups.route,
                        onClick = {
                            navController.navigate(route = Screen.Groups.route) {
                                popUpTo(Screen.Groups.route) {
                                    inclusive = true
                                }
                            }
                            selectedDestination = Screen.Groups.route },
                        icon = {
                            Icon(
                                Icons.Default.Groups,
                                contentDescription = Screen.Groups.route
                            ) },
                        label = { Screen.Groups.label?.let { Text(it) } }
                    )
                    NavigationBarItem(
                        selected = selectedDestination == Screen.Profile.route,
                        onClick = {
                            navController.navigate(route = Screen.Profile.route)
                            selectedDestination = Screen.Profile.route },
                        icon = {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = Screen.Profile.route
                            ) },
                        label = { Screen.Profile.label?.let { Text(it) } }
                    )
                }
            }
        ) { innerPadding ->
            Surface(modifier = Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route
                ) {
                    composable(Screen.Login.route) {
                        LoginScreen(
                            navController = navController,
                            onLoginSuccess = { user ->
                                navController.navigate(Screen.Groups.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.Register.route) {
                        RegisterScreen(navigation = navController)
                    }
                    composable(Screen.Groups.route) {
                        GroupsScreen(navigation = navController)
                    }
                    composable(
                        Screen.GroupDetails.route
                    ) { backStackEntry ->
                        val groupId = backStackEntry.arguments?.getString("groupId")
                        groupId?.let {id ->
                            GroupScreen(groupId = id, navigation = navController)
                        }
                    }

                    composable(Screen.CreateGroup.route) {
                        CreateGroupScreen(navigation = navController)
                    }

                    composable(Screen.EditGroup.route) {
                        EditGroupScreen(navigation = navController)
                    }

                    composable(Screen.NewExpense.route) {
                        NewExpenseScreen(navigation = navController)
                    }
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
    object CreateGroup : Screen("createGroup", "Create Group")
    object EditGroup : Screen("editGroup", "Edit Group")
    object Login : Screen("login", "Login")
    object Register: Screen("register", "Register")
    object NewExpense: Screen("newExpense", "New Expense")

    object GroupDetails : Screen("groupDetails/{groupId}", null) {
        fun createRoute(groupId: String) = "groupDetails/$groupId"
    }
}