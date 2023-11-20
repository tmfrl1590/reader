package com.json.reader.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.json.reader.screen.ReaderSplashScreen
import com.json.reader.screen.detail.BookDetailsScreen
import com.json.reader.screen.home.Home
import com.json.reader.screen.home.HomeScreenViewModel
import com.json.reader.screen.login.ReaderLoginScreen
import com.json.reader.screen.search.BooksSearchViewModel
import com.json.reader.screen.search.SearchScreen
import com.json.reader.screen.stats.ReaderStatsScreen
import com.json.reader.screen.update.ReaderBookUpdateScreen

@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ReaderScreens.SplashScreen.name) {
        composable(ReaderScreens.SplashScreen.name) {
            ReaderSplashScreen(navController = navController)
        }

        composable(ReaderScreens.ReaderHomeScreen.name) {
            val homeViewModel = hiltViewModel<HomeScreenViewModel>()
            Home(navController = navController, homeViewModel)
        }

        composable(ReaderScreens.LoginScreen.name) {
            ReaderLoginScreen(navController = navController)
        }

        composable(ReaderScreens.ReaderStatsScreen.name) {
            val homeViewModel = hiltViewModel<HomeScreenViewModel>()
            ReaderStatsScreen(navController = navController, viewModel = homeViewModel)
        }

        composable(ReaderScreens.SearchScreen.name) {
            val viewModel = hiltViewModel<BooksSearchViewModel>()
            SearchScreen(navController = navController, viewModel = viewModel)
        }

        val detailName = ReaderScreens.DetailScreen.name
        composable(
            route = "$detailName/{bookId}",
            arguments = listOf(navArgument("bookId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("bookId").let{
                BookDetailsScreen(navController = navController, bookId = it.toString())
            }
        }

        val updateName = ReaderScreens.UpdateScreen.name
        composable(
            route = "$updateName/{bookItemId}",
            arguments = listOf(navArgument("bookItemId"){
                type = NavType.StringType
            })
        ){ navBackStackEntry ->
            navBackStackEntry.arguments?.getString("bookItemId").let {
                ReaderBookUpdateScreen(navController = navController, bookItemId = it.toString())
            }
        }
    }
}