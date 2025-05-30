package com.swiftcause.swiftcause_android

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swiftcause.swiftcause_android.screens.HomeScreen
import com.swiftcause.swiftcause_android.screens.WelcomeScreen

@Composable
fun NavController(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.welcomeScreen,
        builder = {
            composable(Routes.welcomeScreen){
                WelcomeScreen(navController)
            }

            composable(Routes.homeScreen + "/{name}"){
                val name = it.arguments?.getString("name")
                HomeScreen(navController, name?:"User")
            }
        }
    )
}