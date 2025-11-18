package com.example.meal_mate.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.meal_mate.ui.components.GlassSurface
import com.example.meal_mate.viewmodel.ThemeViewModel

@Composable
fun SettingsScreen(themeViewModel: ThemeViewModel) {

    val context = LocalContext.current
    val isDarkTheme = themeViewModel.isDarkTheme.value
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text("Control room", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Tune MealMate to your vibe",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        GlassSurface(
            modifier = Modifier.fillMaxWidth()
        ) {
            SettingRow(
                title = "Dark mode",
                subtitle = "Match the system for seamless experience",
                icon = Icons.Rounded.DarkMode,
                trailing = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { themeViewModel.toggleTheme(it) }
                    )
                }
            )
        }

        GlassSurface(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                SettingRow(
                    title = "Send feedback",
                    subtitle = "We reply within 24 hours",
                    icon = Icons.Rounded.Email,
                    onClick = {
                        val emailIntent = Intent(
                            Intent.ACTION_SENDTO,
                            Uri.parse("mailto:mealmateapp@gmail.com?subject=Feedback for MealMate")
                        )
                        context.startActivity(emailIntent)
                    }
                )
                SettingRow(
                    title = "Rate MealMate",
                    subtitle = "Share the love with your circle",
                    icon = Icons.Rounded.Star,
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.example.meal_mate")
                        )
                        context.startActivity(intent)
                    }
                )
            }
        }

        GlassSurface(
            modifier = Modifier.fillMaxWidth()
        ) {
            SettingRow(
                title = "About MealMate",
                subtitle = "v1.0.0 · Crafted by Mayank Sharma",
                icon = Icons.Rounded.Info
            )
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val rowModifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassSurface(
            modifier = Modifier.size(46.dp),
            shape = CircleShape,
            borderColor = Color.Transparent,
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        trailing?.invoke()
    }
}
