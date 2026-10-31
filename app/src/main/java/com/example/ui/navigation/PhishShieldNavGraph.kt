package com.example.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.model.UserProfile
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.ScannerScreen
import com.example.viewmodel.ScannerViewModel

class PhishShieldNavigationActions(private val navController: NavHostController) {
    fun navigateToHome() {
        navController.navigate(NavDestination.Home.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToScanner() {
        navController.navigate(NavDestination.Scanner.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToResults(scanId: String) {
        navController.navigate(NavDestination.Result.createRoute(scanId))
    }

    fun navigateToDashboard() {
        navController.navigate(NavDestination.Dashboard.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToAbout() {
        navController.navigate(NavDestination.About.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToAuth() {
        navController.navigate(NavDestination.Auth.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}

@Composable
fun rememberPhishShieldNavigationActions(navController: NavHostController): PhishShieldNavigationActions {
    return remember(navController) {
        PhishShieldNavigationActions(navController)
    }
}

@Composable
fun PhishShieldNavGraph(
    navController: NavHostController,
    viewModel: ScannerViewModel,
    userProfile: UserProfile,
    modifier: Modifier = Modifier,
    startDestination: String = NavDestination.Home.route,
    navActions: PhishShieldNavigationActions = rememberPhishShieldNavigationActions(navController)
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(220)) },
        exitTransition = { fadeOut(animationSpec = tween(200)) },
        popEnterTransition = { fadeIn(animationSpec = tween(220)) },
        popExitTransition = { fadeOut(animationSpec = tween(200)) }
    ) {
        // Destination 1: Home (Landing Page)
        composable(route = NavDestination.Home.route) {
            LandingScreen(
                onNavigateToScanner = { navActions.navigateToScanner() },
                onNavigateToAbout = { navActions.navigateToAbout() }
            )
        }

        // Destination 2: Scanner (Security Command Center)
        composable(route = NavDestination.Scanner.route) {
            ScannerScreen(
                repository = viewModel.repository,
                viewModel = viewModel,
                onScanComplete = { result ->
                    navActions.navigateToResults(result.id)
                },
                onNavigateBack = {
                    navActions.navigateBack()
                }
            )
        }

        // Destination 3: Results (Security Audit Report)
        composable(
            route = NavDestination.Result.route,
            arguments = listOf(
                navArgument("scanId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val scanId = backStackEntry.arguments?.getString("scanId") ?: ""
            val result = viewModel.getScanById(scanId)
            if (result != null) {
                ResultScreen(
                    result = result,
                    onScanAgain = {
                        navActions.navigateToScanner()
                    },
                    onBack = {
                        navActions.navigateBack()
                    }
                )
            } else {
                // Fallback to Scanner if scan ID is unresolvable
                ScannerScreen(
                    repository = viewModel.repository,
                    onScanComplete = { res ->
                        navActions.navigateToResults(res.id)
                    },
                    onNavigateBack = {
                        navActions.navigateBack()
                    }
                )
            }
        }

        // Destination 4: User Dashboard (Audit Logs & Telemetry)
        composable(route = NavDestination.Dashboard.route) {
            DashboardScreen(
                repository = viewModel.repository,
                onViewReport = { result ->
                    navActions.navigateToResults(result.id)
                },
                onNavigateToScan = {
                    navActions.navigateToScanner()
                }
            )
        }

        // Destination 5: About & Privacy
        composable(route = NavDestination.About.route) {
            AboutScreen()
        }

        // Destination 6: Auth & Security Profile
        composable(route = NavDestination.Auth.route) {
            AuthScreen(
                userProfile = userProfile,
                onUpdateProfile = { viewModel.updateProfile(it) }
            )
        }
    }
}
