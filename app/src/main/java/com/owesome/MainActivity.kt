package com.owesome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.owesome.di.appModule
import com.owesome.ui.screens.Groups
import com.owesome.ui.screens.GroupsScreen
import com.owesome.ui.theme.OweSomeTheme
import com.owesome.ui.viewmodels.NavViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin{
            androidLogger()
            androidContext(this@MainActivity)
            modules(appModule)
        }

        enableEdgeToEdge()
        setContent {
            OweSome()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OweSome(viewModel: NavViewModel = koinActivityViewModel()) {
    val navController = rememberNavController()
    var selectedDestination by rememberSaveable { mutableStateOf(Screen.Groups.route) }

    val headerTitle by viewModel.title.collectAsState()

    viewModel.setTitle("Test")

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
                        )
                    },
                    navigationIcon = {
                        if (navController.previousBackStackEntry != null) {
                            IconButton(onClick = {navController.popBackStack()}) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Localized description"
                                )
                            }
                        }
                    },
                    modifier = Modifier.dropShadow(
                        shape = RoundedCornerShape(0.dp),
                        shadow = Shadow(
                            radius = 6.dp,
                            spread = 6.dp,
                            color = Color(0x40000000),
                            offset = DpOffset(x = 0.dp, 4.dp)
                        )
                    )
                )
            },
            bottomBar = {
                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                    NavigationBarItem(
                        selected = selectedDestination == Screen.Groups.route,
                        onClick = {
                            navController.navigate(route = Screen.Groups.route)
                            selectedDestination = Screen.Groups.route
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
                        selected = selectedDestination == Screen.Profile.route,
                        onClick = {
                            navController.navigate(route = Screen.Profile.route)
                            selectedDestination = Screen.Profile.route
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

    object GroupDetails : Screen("groupDetails/{groupId}", null) {
        fun createRoute(groupId: Int) = "groupDetails/$groupId"
    }
}