package com.swiftcause.swiftcause_android.ui.screen.campaign_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun CampaignListScreen(navController: NavController, name : String){

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Text("Hello $name (campaign screen)")
    }
}