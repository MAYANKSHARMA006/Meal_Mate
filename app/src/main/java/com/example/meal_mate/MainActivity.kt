package com.example.meal_mate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.meal_mate.screens.SplashScreen
import com.example.meal_mate.ui.theme.MealMateTheme
import com.example.meal_mate.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // ⭐ Splash logic added here
            var showSplash by remember { mutableStateOf(true) }

            MealMateTheme(darkTheme = themeViewModel.isDarkTheme.value) {
                if (showSplash) {
                    SplashScreen(
                        onFinish = { showSplash = false }
                    )
                } else {
                    MainScreen(themeViewModel)
                }
            }
        }
    }
}
