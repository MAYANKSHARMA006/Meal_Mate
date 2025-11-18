package com.example.meal_mate


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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
import com.example.meal_mate.ui.components.AuroraBackground
import com.example.meal_mate.viewmodel.ThemeViewModel
import kotlinx.coroutines.launch
import androidx.compose.material3.rememberDrawerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(themeViewModel: ThemeViewModel) {

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // ⭐ Added Analytics item here
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Wheel,
        BottomNavItem.History,
        BottomNavItem.Analytics,   // <-- NEW
        BottomNavItem.Settings
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                items = items,
                navController = navController,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                    scope.launch { drawerState.close() }
                }
            )
        },
        scrimColor = Color.Black.copy(alpha = 0.3f)
    ) {
        AuroraBackground {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                topBar = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val currentLabel = items.firstOrNull { it.route == currentRoute }?.title ?: "MealMate"

                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Rounded.Menu,
                                    contentDescription = "Menu",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        },
                        title = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = currentLabel,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "Navigate your day",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.Home.route,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(NavRoutes.Home.route) { HomeScreen() }
                        composable(NavRoutes.Wheel.route) { WheelScreen() }
                        composable(NavRoutes.Settings.route) { SettingsScreen(themeViewModel) }
                        composable("history") { HistoryScreen() }
                        composable("analytics") { AnalyticsScreen() }
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerContent(
    items: List<BottomNavItem>,
    navController: NavHostController,
    onNavigate: (String) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalDrawerSheet(
        drawerTonalElevation = 0.dp,
        drawerContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "MealMate",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Everything you need, in one menu",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            items.forEach { item ->
                NavigationDrawerItem(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Column {
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = when (item) {
                                    BottomNavItem.Home -> "Dashboard & quick glance"
                                    BottomNavItem.Wheel -> "Spin the culinary wheel"
                                    BottomNavItem.History -> "Track previous meals"
                                    BottomNavItem.Analytics -> "Insights & highlights"
                                    BottomNavItem.Settings -> "Themes & preferences"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    selected = currentRoute == item.route,
                    onClick = { onNavigate(item.route) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        unselectedContainerColor = Color.Transparent,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}
