package com.example.meal_mate


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.meal_mate.navigation.BottomNavItem
import com.example.meal_mate.navigation.NavRoutes
import com.example.meal_mate.screens.AnalyticsScreen
import com.example.meal_mate.screens.SettingsScreen
import com.example.meal_mate.screens.WheelScreen
import com.example.meal_mate.ui.HistoryScreen
import com.example.meal_mate.ui.HomeScreen
import com.example.meal_mate.viewmodel.ThemeViewModel

@Composable
fun MainScreen(themeViewModel: ThemeViewModel) {

    val navController = rememberNavController()

    // ⭐ Added Analytics item here
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Wheel,
        BottomNavItem.History,
        BottomNavItem.Analytics,   // <-- NEW
        BottomNavItem.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry = navController.currentBackStackEntryAsState().value
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        label = { Text(item.title) },
                        icon = { Icon(item.icon, contentDescription = item.title) }
                    )
                }
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = NavRoutes.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(NavRoutes.Home.route) { HomeScreen() }
            composable(NavRoutes.Wheel.route) { WheelScreen() }
            composable(NavRoutes.Settings.route) { SettingsScreen(themeViewModel) }
            composable("history") { HistoryScreen() }

            // ⭐ Added Analytics Route (Step 18C)
            composable("analytics") { AnalyticsScreen() }
        }
    }
}
