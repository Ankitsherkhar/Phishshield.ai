package com.example.ui.container

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.ui.navigation.NavDestination
import com.example.ui.navigation.PhishShieldNavGraph
import com.example.ui.navigation.rememberPhishShieldNavigationActions
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberNavyDarker
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberNavySurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ScannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    viewModel: ScannerViewModel,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val userProfile by viewModel.userProfile.collectAsState()
    val navActions = rememberPhishShieldNavigationActions(navController)

    val isTopLevelDestination = NavDestination.bottomNavItems.any { it.route == currentRoute }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 680.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = CyberNavyDarker,
            topBar = {
                if (currentRoute != NavDestination.Scanner.route) {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!isTopLevelDestination) {
                                    IconButton(
                                        onClick = { navActions.navigateBack() },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(CyberNavySurfaceVariant, CircleShape)
                                            .testTag("app_bar_back_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = "Navigate Back",
                                            tint = ElectricCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.phish_shield_logo),
                                            contentDescription = "PhishShield AI Logo",
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                }

                                Column {
                                    Text(
                                        text = "PhishShield AI",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Detect. Defend. Stay Safe.",
                                        fontSize = 10.sp,
                                        color = ElectricCyan,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        },
                        actions = {
                            Box(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .background(ElectricCyan.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                                    .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 9.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(ElectricCyan, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "SECURE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricCyan,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = CyberNavyDarker,
                            titleContentColor = TextPrimary
                        )
                    )
                }
            },
            bottomBar = {
                if (!isWideScreen && currentRoute != NavDestination.Scanner.route) {
                    NavigationBar(
                        containerColor = CyberNavySurface,
                        contentColor = ElectricCyan,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .border(1.dp, CyberCardBorder.copy(alpha = 0.5f))
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("main_navigation_bar")
                    ) {
                        NavDestination.bottomNavItems.forEach { item ->
                            val isSelected = currentRoute == item.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    when (item) {
                                        NavDestination.Home -> navActions.navigateToHome()
                                        NavDestination.Scanner -> navActions.navigateToScanner()
                                        NavDestination.Dashboard -> navActions.navigateToDashboard()
                                        NavDestination.About -> navActions.navigateToAbout()
                                        NavDestination.Auth -> navActions.navigateToAuth()
                                        else -> Unit
                                    }
                                },
                                icon = {
                                    item.icon?.let {
                                        Icon(
                                            imageVector = it,
                                            contentDescription = item.title,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = item.title,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = CyberNavyDarker,
                                    selectedTextColor = ElectricCyan,
                                    indicatorColor = ElectricCyan,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Adaptive NavigationRail for tablet/expanded displays
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = CyberNavySurface,
                        contentColor = ElectricCyan,
                        modifier = Modifier
                            .fillMaxHeight()
                            .border(1.dp, CyberCardBorder)
                            .testTag("main_navigation_rail")
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        NavDestination.bottomNavItems.forEach { item ->
                            val isSelected = currentRoute == item.route
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = {
                                    when (item) {
                                        NavDestination.Home -> navActions.navigateToHome()
                                        NavDestination.Scanner -> navActions.navigateToScanner()
                                        NavDestination.Dashboard -> navActions.navigateToDashboard()
                                        NavDestination.About -> navActions.navigateToAbout()
                                        NavDestination.Auth -> navActions.navigateToAuth()
                                        else -> Unit
                                    }
                                },
                                icon = {
                                    item.icon?.let {
                                        Icon(
                                            imageVector = it,
                                            contentDescription = item.title,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = item.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = CyberNavyDarker,
                                    selectedTextColor = ElectricCyan,
                                    indicatorColor = ElectricCyan,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }

                // Core App Screen Container delegating to PhishShieldNavGraph
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 840.dp)
                    ) {
                        PhishShieldNavGraph(
                            navController = navController,
                            viewModel = viewModel,
                            userProfile = userProfile,
                            navActions = navActions
                        )
                    }
                }
            }
        }
    }
}
