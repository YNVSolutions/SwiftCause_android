package com.swiftcause.swiftcause_android.ui.screen.campaign_details

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.swiftcause.swiftcause_android.ui.navigation.Routes

@Composable
fun CampaignDetailsScreen(navController: NavController, campId : String){
    Column(modifier = Modifier.fillMaxSize().padding(10.dp), verticalArrangement = Arrangement.SpaceAround, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("This is the campaign details screen of campId: $campId")
        Text("You can see all the details of the selected campaign that can be fetched by using the campId got in the params")
        Button(onClick = {
//            navController.navigate() //navigate to the payment options selection screen
//            Toast.makeText()
            navController.navigate(Routes.paymentOptionsScreen + "/$campId")
        }) {
            Text("Donate")
        }
    }
}