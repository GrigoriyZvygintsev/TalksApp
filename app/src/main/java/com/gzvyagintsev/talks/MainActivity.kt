package com.gzvyagintsev.talks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gzvyagintsev.talks.navigation.AppNavHost
import com.gzvyagintsev.talks.navigation.Screen
import com.gzvyagintsev.talks.ui.theme.AccentColor
import com.gzvyagintsev.talks.ui.theme.BgColor
import com.gzvyagintsev.talks.ui.theme.TextPrimary
import com.gzvyagintsev.talks.ui.theme.TextSecondary
import com.gzvyagintsev.talks.ui.theme.TalksAppTheme

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val testTag: String
)

val bottomNavItems = listOf(
    BottomNavItem("Главная", Icons.Default.Home, Screen.Home.route, "nav_home"),
    BottomNavItem("Доклады", Icons.Default.List, Screen.TalksList.route, "nav_talks"),
    BottomNavItem("Мемы", Icons.Default.Face, Screen.Memes.route, "nav_memes"),
    BottomNavItem("Чат", Icons.Default.MailOutline, Screen.Chat.route, "nav_chat"),
    BottomNavItem("Контакт", Icons.Default.Email, Screen.Contacts.route, "nav_contact"),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        com.gzvyagintsev.talks.data.ServiceLocator.init(this)
        setContent {
            TalksAppTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Hide bottom bar on detail screens
                val showBottomBar = currentRoute in bottomNavItems.map { it.route }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = BgColor,
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(
                                currentRoute = currentRoute,
                                onItemClick = { route ->
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
                    }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    NavigationBar(
        containerColor = BgColor,
        modifier = Modifier.testTag("bottom_navigation")
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onItemClick(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TextPrimary,
                    selectedTextColor = TextPrimary,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = AccentColor.copy(alpha = 0.2f)
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}
