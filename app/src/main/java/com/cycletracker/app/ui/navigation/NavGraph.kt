package com.cycletracker.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.cycletracker.app.ui.screen.*

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    object Tips : Screen("tips", "Tips", Icons.Default.Lightbulb)
    object History : Screen("history", "History", Icons.Default.History)
    object LogDay : Screen("log/{epochDay}", "Log", Icons.Default.Edit) {
        fun routeFor(epochDay: Long) = "log/$epochDay"
    }
}

private val bottomNavItems = listOf(Screen.Home, Screen.Calendar, Screen.Tips, Screen.History)

@Composable
fun CycleTrackerApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val navBackStack by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStack?.destination
            val showBottom = bottomNavItems.any { it.route == currentDestination?.route }

            if (showBottom) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(onLogDay = { epochDay ->
                    navController.navigate(Screen.LogDay.routeFor(epochDay))
                })
            }
            composable(Screen.Calendar.route) {
                CalendarScreen(onDayClick = { epochDay ->
                    navController.navigate(Screen.LogDay.routeFor(epochDay))
                })
            }
            composable(Screen.Tips.route) { TipsScreen() }
            composable(Screen.History.route) { HistoryScreen() }
            composable(
                route = Screen.LogDay.route,
                arguments = listOf(navArgument("epochDay") { type = NavType.LongType })
            ) { backStack ->
                val epochDay = backStack.arguments?.getLong("epochDay")
                    ?: java.time.LocalDate.now().toEpochDay()
                LogDayScreen(
                    epochDay = epochDay,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
