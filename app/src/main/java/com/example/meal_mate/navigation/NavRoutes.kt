package com.example.meal_mate.navigation



sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Wheel : NavRoutes("wheel")
    object Settings : NavRoutes("settings")
}
