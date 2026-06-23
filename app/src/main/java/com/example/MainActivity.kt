package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.EventSession
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.DasaDeepNavy
import com.example.ui.theme.DasaGold
import com.example.ui.theme.BorderColor
import com.example.ui.viewmodel.EventViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppLayout()
            }
        }
    }
}

@Composable
fun MainAppLayout() {
    val viewModel: EventViewModel = viewModel()
    val userProfile by viewModel.userProfile.collectAsState()

    var currentTab by remember { mutableStateOf(0) } // 0=Home, 1=Agenda, 2=Networking, 3=Directory, 4=AI, 5=Badge
    var selectedSessionForDetail by remember { mutableStateOf<EventSession?>(null) }
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen(onSplashFinished = { showSplash = false })
    } else if (userProfile == null || userProfile?.isLoggedIn == false) {
        AuthScreen(viewModel = viewModel)
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = DasaDeepNavy,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .testTag("main_navigation_bar")
                        .border(1.dp, BorderColor)
                ) {
                    NavigationBarItem(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DasaDeepNavy,
                            selectedTextColor = DasaGold,
                            indicatorColor = DasaGold,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Agenda") },
                        label = { Text("Agenda", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DasaDeepNavy,
                            selectedTextColor = DasaGold,
                            indicatorColor = DasaGold,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_agenda")
                    )

                    NavigationBarItem(
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 },
                        icon = { Icon(Icons.Default.Group, contentDescription = "Networking") },
                        label = { Text("Network", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DasaDeepNavy,
                            selectedTextColor = DasaGold,
                            indicatorColor = DasaGold,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_networking")
                    )

                    NavigationBarItem(
                        selected = currentTab == 3,
                        onClick = { currentTab = 3 },
                        icon = { Icon(Icons.Default.Business, contentDescription = "Directory") },
                        label = { Text("Directory", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DasaDeepNavy,
                            selectedTextColor = DasaGold,
                            indicatorColor = DasaGold,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_directory")
                    )

                    NavigationBarItem(
                        selected = currentTab == 4,
                        onClick = { currentTab = 4 },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Assistant") },
                        label = { Text("AI Intellect", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DasaDeepNavy,
                            selectedTextColor = DasaGold,
                            indicatorColor = DasaGold,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_ai")
                    )

                    NavigationBarItem(
                        selected = currentTab == 5,
                        onClick = { currentTab = 5 },
                        icon = { Icon(Icons.Default.QrCode, contentDescription = "Digital Pass") },
                        label = { Text("Pass", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DasaDeepNavy,
                            selectedTextColor = DasaGold,
                            indicatorColor = DasaGold,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_badge")
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    0 -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToTab = { currentTab = it },
                        onSelectSession = {
                            selectedSessionForDetail = it
                            currentTab = 1 // Switch to Agenda tab to open detailed evaluation dialog
                        }
                    )
                    1 -> AgendaScreen(
                        viewModel = viewModel,
                        selectedSessionState = selectedSessionForDetail,
                        onSelectSession = { selectedSessionForDetail = it }
                    )
                    2 -> NetworkingScreen(viewModel = viewModel)
                    3 -> DirectoryScreen(viewModel = viewModel)
                    4 -> AIAssistantScreen(viewModel = viewModel)
                    5 -> ProfileBadgeScreen(viewModel = viewModel)
                }
            }
        }
    }
}

