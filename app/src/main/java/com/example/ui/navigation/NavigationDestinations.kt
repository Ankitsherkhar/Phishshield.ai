package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    val testTag: String = ""
) {
    object Home : NavDestination(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home,
        testTag = "nav_item_home"
    )

    object Scanner : NavDestination(
        route = "scanner",
        title = "Scanner",
        icon = Icons.Default.Security,
        testTag = "nav_item_scanner"
    )

    object Dashboard : NavDestination(
        route = "dashboard",
        title = "Audit Log",
        icon = Icons.Default.Assessment,
        testTag = "nav_item_dashboard"
    )

    object About : NavDestination(
        route = "about",
        title = "About",
        icon = Icons.Default.Info,
        testTag = "nav_item_about"
    )

    object Auth : NavDestination(
        route = "auth",
        title = "Account",
        icon = Icons.Default.AccountCircle,
        testTag = "nav_item_auth"
    )

    object Result : NavDestination(
        route = "result/{scanId}",
        title = "Security Report"
    ) {
        fun createRoute(scanId: String): String = "result/$scanId"
    }

    companion object {
        val bottomNavItems = listOf(Home, Scanner, Dashboard, About, Auth)
    }
}
