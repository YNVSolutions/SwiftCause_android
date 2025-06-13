package com.swiftcause.swiftcause_android.ui.screen.dummy_payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.swiftcause.swiftcause_android.ui.navigation.Routes

@Composable
fun DummyPaymentScreen(campId: String, amount: String, navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dummy stripe payment gateway screen...")
        Text("Paying £$amount to campId: $campId")
        Button(onClick = {
            navController.navigate(Routes.thankYouScreen)
        }) { Text("payment_success") }
    }
}