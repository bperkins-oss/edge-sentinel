/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024 BP22 Intel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.bp22intel.edgesentinel.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bp22intel.edgesentinel.ui.about.AboutScreen
import com.bp22intel.edgesentinel.ui.dashboard.DashboardScreen
import com.bp22intel.edgesentinel.ui.alerts.AlertListScreen
import com.bp22intel.edgesentinel.ui.alerts.AlertDetailScreen
import com.bp22intel.edgesentinel.ui.cellinfo.CellInfoScreen
import com.bp22intel.edgesentinel.ui.mesh.MeshScreen
import com.bp22intel.edgesentinel.ui.onboarding.OnboardingScreen
import com.bp22intel.edgesentinel.ui.settings.SettingsScreen
import com.bp22intel.edgesentinel.ui.wifi.WifiScreen

/**
 * Navigation routes for the app.
 */
object Routes {
    const val ONBOARDING = "onboarding"
    const val DASHBOARD = "dashboard"
    const val ALERTS = "alerts"
    const val ALERT_DETAIL = "alert_detail/{alertId}"
    const val CELL_INFO = "cell_info"
    const val WIFI = "wifi"
    const val MESH = "mesh"
    const val SETTINGS = "settings"
    const val ABOUT = "about"

    fun alertDetail(alertId: Long): String = "alert_detail/$alertId"
}

/**
 * Bottom navigation tab definitions.
 */
enum class BottomNavTab(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    DASHBOARD(Routes.DASHBOARD, "Dashboard", Icons.Default.Home),
    ALERTS(Routes.ALERTS, "Alerts", Icons.Default.Notifications),
    CELL_INFO(Routes.CELL_INFO, "Cell Info", Icons.Default.SignalCellular4Bar),
    WIFI(Routes.WIFI, "WiFi", Icons.Default.Wifi),
    MESH(Routes.MESH, "Mesh", Icons.Default.Share),
    SETTINGS(Routes.SETTINGS, "Settings", Icons.Default.Settings)
}

@Composable
fun EdgeSentinelNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Hide bottom bar on onboarding, about, and alert detail screens
    val showBottomBar = currentDestination?.route?.let { route ->
        route != Routes.ONBOARDING && route != Routes.ABOUT && route != Routes.ALERT_DETAIL
    } ?: true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomNavTab.entries.forEach { tab ->
                        NavigationBarItem(
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            selected = currentDestination?.hierarchy?.any {
                                it.route == tab.route
                            } == true,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
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
            startDestination = Routes.ONBOARDING,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn() + slideInHorizontally { it / 4 } },
            exitTransition = { fadeOut() + slideOutHorizontally { -it / 4 } },
            popEnterTransition = { fadeIn() + slideInHorizontally { -it / 4 } },
            popExitTransition = { fadeOut() + slideOutHorizontally { it / 4 } }
        ) {
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onOnboardingComplete = {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    onAlertClick = { alert ->
                        navController.navigate(Routes.alertDetail(alert.id))
                    },
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Routes.ALERTS) {
                AlertListScreen(
                    onAlertClick = { alert ->
                        navController.navigate(Routes.alertDetail(alert.id))
                    }
                )
            }
            composable(
                route = Routes.ALERT_DETAIL,
                arguments = listOf(navArgument("alertId") { type = NavType.LongType })
            ) { backStackEntry ->
                val alertId = backStackEntry.arguments?.getLong("alertId") ?: 0L
                AlertDetailScreen(alertId = alertId)
            }
            composable(Routes.CELL_INFO) {
                CellInfoScreen()
            }
            composable(Routes.WIFI) {
                WifiScreen()
            }
            composable(Routes.MESH) {
                MeshScreen()
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onNavigateToAbout = {
                        navController.navigate(Routes.ABOUT)
                    }
                )
            }
            composable(Routes.ABOUT) {
                AboutScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
