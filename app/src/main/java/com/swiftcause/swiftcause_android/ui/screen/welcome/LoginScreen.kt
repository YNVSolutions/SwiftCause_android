package com.swiftcause.swiftcause_android.ui.screen.welcome

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.swiftcause.swiftcause_android.ui.navigation.Routes

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current;
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val response = IdpResponse.fromResultIntent(result.data)
        if (result.resultCode == Activity.RESULT_OK) {
            // login successful
            val user = FirebaseAuth.getInstance().currentUser
            navController.navigate(Routes.campaignListScreen + "/${user?.displayName}")
            Log.d("Fire-Auth", "the user is , ${user?.email}, ${user?.uid}")
        } else {
            Log.d(
                "Fire-Auth",
                "the login was not successful due to : ${response?.error?.errorCode}"
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to SwiftCause", fontSize = 24.sp)
        Button(onClick = {
            val provider = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build()
            )

            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .setIsSmartLockEnabled(false) // for testing
                .build()

            launcher.launch(signInIntent)
        }) {
            Text("Sign in", fontSize = 16.sp)
        }
    }
}