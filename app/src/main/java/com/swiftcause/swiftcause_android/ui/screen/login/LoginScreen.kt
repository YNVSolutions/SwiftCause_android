package com.swiftcause.swiftcause_android.ui.screen.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.swiftcause.swiftcause_android.ui.navigation.Routes

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
//    onLoginSuccess: () -> Unit
    navController: NavController
) {
    val authState by authViewModel.authUiState.collectAsState()
    val context = LocalContext.current

    // Launcher for FirebaseUI
    val signInLauncher = rememberLauncherForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
        authViewModel.handleAuthResult(res.idpResponse, res.resultCode)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (authState) {
            AuthUiState.Unauthenticated -> {
                Button(onClick = {
                    authViewModel.launchFirebaseAuthUI(signInLauncher)
                }) {
                    Text("Login with Firebase")
                }

                if (authState is AuthUiState.Error) {
                    val errorMessage = (authState as AuthUiState.Error).message
                    Text("Error: $errorMessage", color = androidx.compose.ui.graphics.Color.Red)
                }
            }
            AuthUiState.Authenticating -> {
                CircularProgressIndicator()
                Text("Authenticating...")
            }
            is AuthUiState.Authenticated -> {
                navController.navigate(Routes.campaignListScreen + "/jitesh")
                Toast.makeText(context, "Logged in successfully!", Toast.LENGTH_SHORT).show()
            }
            else -> Toast.makeText(context, "Some error occurred!", Toast.LENGTH_SHORT).show()
        }
    }
}

