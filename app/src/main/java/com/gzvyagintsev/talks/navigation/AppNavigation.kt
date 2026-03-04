package com.gzvyagintsev.talks.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gzvyagintsev.talks.ui.screens.contacts.ContactsScreen
import com.gzvyagintsev.talks.ui.screens.home.HomeScreen
import com.gzvyagintsev.talks.ui.screens.talks.TalkDetailScreen
import com.gzvyagintsev.talks.ui.screens.talks.TalkViewerScreen
import com.gzvyagintsev.talks.ui.screens.talks.TalksListScreen
import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object TalksList : Screen("talks")
    object TalkDetail : Screen("talk/{slug}") {
        fun createRoute(slug: String) = "talk/$slug"
    }
    object TalkViewer : Screen("viewer/{title}/{url}") {
        fun createRoute(title: String, url: String): String {
            val encodedTitle = URLEncoder.encode(title, "UTF-8")
            val encodedUrl = URLEncoder.encode(url, "UTF-8")
            return "viewer/$encodedTitle/$encodedUrl"
        }
    }
    object Memes : Screen("memes")
    object Chat : Screen("chat")
    object Contacts : Screen("contacts")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTalks = {
                    navController.navigate(Screen.TalksList.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToMemes = {
                    navController.navigate(Screen.Memes.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToContacts = {
                    navController.navigate(Screen.Contacts.route) {
                        launchSingleTop = true
                    }
                },
                onTalkClick = { slug ->
                    navController.navigate(Screen.TalkDetail.createRoute(slug))
                }
            )
        }
        composable(Screen.TalksList.route) {
            TalksListScreen(
                onTalkClick = { slug ->
                    navController.navigate(Screen.TalkDetail.createRoute(slug))
                }
            )
        }
        composable(
            route = Screen.TalkDetail.route,
            arguments = listOf(navArgument("slug") { type = NavType.StringType })
        ) { backStackEntry ->
            val slug = backStackEntry.arguments?.getString("slug") ?: return@composable
            TalkDetailScreen(
                slug = slug,
                onBack = { navController.popBackStack() },
                onOpenViewer = { title, url ->
                    navController.navigate(Screen.TalkViewer.createRoute(title, url))
                }
            )
        }
        composable(
            route = Screen.TalkViewer.route,
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("url") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title")?.let {
                URLDecoder.decode(it, "UTF-8")
            } ?: "Доклад"
            val url = backStackEntry.arguments?.getString("url")?.let {
                URLDecoder.decode(it, "UTF-8")
            } ?: return@composable
            TalkViewerScreen(
                title = title,
                url = url,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Memes.route) {
            com.gzvyagintsev.talks.ui.screens.memes.MemesScreen()
        }
        composable(Screen.Chat.route) {
            com.gzvyagintsev.talks.ui.screens.chat.ChatScreen(
                onTalkClick = { slug ->
                    navController.navigate(Screen.TalkDetail.createRoute(slug))
                }
            )
        }
        composable(Screen.Contacts.route) {
            ContactsScreen()
        }
    }
}
