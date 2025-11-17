package com.example.meal_mate.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {

    // Home
    object Home : BottomNavItem(
        "Home",
        Icons.Filled.Home,
        NavRoutes.Home.route
    )

    // Wheel
    object Wheel : BottomNavItem(
        "Wheel",
        Icons.Filled.Circle,     // Replaced WheelchairPickup
        NavRoutes.Wheel.route
    )

    // History
    object History : BottomNavItem(
        "History",
        Icons.Default.History,
        "history"
    )

    // Analytics
    object Analytics : BottomNavItem(
        "Analytics",
        Icons.Default.Analytics,
        "analytics"
    )

    // Settings
    object Settings : BottomNavItem(
        "Settings",
        Icons.Filled.Settings,
        NavRoutes.Settings.route
    )

}
