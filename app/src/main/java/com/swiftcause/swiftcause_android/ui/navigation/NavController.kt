package com.swiftcause.swiftcause_android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swiftcause.swiftcause_android.ui.screen.welcome.LoginScreen
import com.swiftcause.swiftcause_android.ui.screen.campaign_list.CampaignListScreen


@Composable
fun NavController(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.loginScreen,
        builder = {

            composable(Routes.loginScreen){
                LoginScreen(navController)
            }

            composable(Routes.campaignListScreen + "/{name}"){
                val name = it.arguments?.getString("name")
                CampaignListScreen(navController, name?:"User")
            }
        }
    )
}