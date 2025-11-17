package com.example.meal_mate.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.meal_mate.viewmodel.ThemeViewModel

@Composable
fun SettingsScreen(themeViewModel: ThemeViewModel) {

    val context = LocalContext.current
    val isDarkTheme = themeViewModel.isDarkTheme.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(25.dp))

        // ---------------- THEME SWITCH ----------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark Mode", style = MaterialTheme.typography.titleMedium)
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { themeViewModel.toggleTheme(it) }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // ---------------- FEEDBACK ----------------
        Text(
            text = "Send Feedback",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.clickable {
                val emailIntent = Intent(
                    Intent.ACTION_SENDTO,
                    Uri.parse("mailto:mealmateapp@gmail.com?subject=Feedback for MealMate")
                )
                context.startActivity(emailIntent)
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        // ---------------- ABOUT APP ----------------
        Text("About App", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "MealMate is your smart meal decision app. It helps you choose meals based on mood, time, and budget while tracking expenses.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("Version: 1.0.0", style = MaterialTheme.typography.bodyMedium)
        Text("Developed by: Mayank Sharma", style = MaterialTheme.typography.bodyMedium)
    }
}
