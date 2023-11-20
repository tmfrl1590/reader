package com.json.reader.navigation

import java.lang.IllegalArgumentException


enum class ReaderScreens {

    SplashScreen,
    LoginScreen,
    CreateAccountScreen,
    SearchScreen,
    ReaderHomeScreen,
    DetailScreen,
    UpdateScreen,
    ReaderStatsScreen;

    companion object {
        fun fromRoute(route:String?): ReaderScreens
            = when(route?.substringBefore("/")){
                SplashScreen.name -> SplashScreen
                LoginScreen.name -> LoginScreen
                CreateAccountScreen.name -> CreateAccountScreen
                SearchScreen.name -> SearchScreen
                DetailScreen.name -> DetailScreen
                UpdateScreen.name -> UpdateScreen
                ReaderStatsScreen.name -> ReaderStatsScreen
                null -> ReaderHomeScreen
                else -> throw IllegalArgumentException("Route $route is not recognized")
            }
    }
}