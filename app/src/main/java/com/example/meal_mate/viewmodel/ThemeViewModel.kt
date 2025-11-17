package com.example.meal_mate.viewmodel


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {

    // Global theme state (false = light, true = dark)
    var isDarkTheme = mutableStateOf(false)

    fun toggleTheme(value: Boolean) {
        isDarkTheme.value = value
    }
}
