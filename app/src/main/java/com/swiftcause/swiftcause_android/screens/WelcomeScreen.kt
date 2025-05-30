package com.swiftcause.swiftcause_android.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.swiftcause.swiftcause_android.Routes

@Composable
fun WelcomeScreen(navController: NavController){
    val context = LocalContext.current;
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val response = IdpResponse.fromResultIntent(result.data)
        if (result.resultCode == Activity.RESULT_OK){
            // login successful
            val user = FirebaseAuth.getInstance().currentUser
            navController.navigate(Routes.homeScreen + "/${user?.displayName}")
            Log.d("Fire-Auth","the user is , ${user?.email}, ${user?.uid}")
        }else{
            Log.d("Fire-Auth", "the login was not successful due to : ${response?.error?.errorCode}")
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
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
        }){
            Text("Sign in")
        }
    }
}