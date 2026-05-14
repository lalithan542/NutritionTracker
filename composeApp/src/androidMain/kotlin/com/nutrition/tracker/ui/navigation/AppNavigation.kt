package com.nutrition.tracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Search : Screen("search")
    object LogFood : Screen("log_food/{foodId}") {
        fun createRoute(foodId: String) = "log_food/$foodId"
    }
    object Profile : Screen("profile")
    object ImageNutrition : Screen("image_nutrition")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard, "Home", Icons.Default.Home),
    BottomNavItem(Screen.Search, "Search", Icons.Default.Search),
    BottomNavItem(Screen.ImageNutrition, "Scan", Icons.Default.CameraAlt),
    BottomNavItem(Screen.Profile, "Profile", Icons.Default.Person)
)
