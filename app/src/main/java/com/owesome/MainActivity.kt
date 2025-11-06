package com.owesome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.owesome.di.appModule
import com.owesome.ui.screens.Groups
import com.owesome.ui.theme.OweSomeTheme
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
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

@Composable
fun OweSome() {
    val navController = rememberNavController()
    var selectedDestination by rememberSaveable { mutableIntStateOf(Screen.GROUPS.ordinal) }

    OweSomeTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                    NavigationBarItem(
                        selected = selectedDestination == Screen.GROUPS.ordinal,
                        onClick = {
                            navController.navigate(route = Screen.GROUPS.route)
                            selectedDestination = Screen.GROUPS.ordinal
                        },
                        icon = {
                            Icon(
                                Icons.Default.Groups,
                                contentDescription = Screen.GROUPS.route
                            )
                        },
                        label = { Text(Screen.GROUPS.label) }
                    )

                    NavigationBarItem(
                        selected = selectedDestination == Screen.PROFILE.ordinal,
                        onClick = {
                            navController.navigate(route = Screen.PROFILE.route)
                            selectedDestination = Screen.PROFILE.ordinal
                        },
                        icon = {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = Screen.PROFILE.route
                            )
                        },
                        label = { Text(Screen.PROFILE.label) }
                    )
                }
            }
        ) { innerPadding ->
            Surface(modifier = Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.GROUPS.route
                ) {
                    Screen.entries.forEach { screen ->
                        composable(screen.route) {
                            when (screen) {
                                Screen.GROUPS -> Groups()
                                Screen.PROFILE -> TODO()
                                Screen.SETTINGS -> TODO()
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class Screen(
    val route: String,
    val label: String,
) {
    GROUPS("groups", "Groups"),
    PROFILE("profile", "Profile"),
    SETTINGS("settings", "Settings"),
}