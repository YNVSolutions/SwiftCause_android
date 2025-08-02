package com.swiftcause.swiftcause_android.ui.screen.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.swiftcause.swiftcause_android.ui.navigation.Routes

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController
) {
    val authState by authViewModel.authUiState.collectAsState()
    val context = LocalContext.current

    // Launcher for FirebaseUI
    val signInLauncher = rememberLauncherForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
        authViewModel.handleAuthResult(res.idpResponse, res.resultCode)
    }
    LaunchedEffect(authState) {
        if (authState == AuthUiState.Unauthenticated){
            authViewModel.launchFirebaseAuthUI(signInLauncher)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (authState) {
            AuthUiState.Unauthenticated -> {}
            AuthUiState.Authenticating -> {}
            is AuthUiState.Error -> {
                Text(
                    text = "Login is required to continue using SwiftCause.",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = {authViewModel.setUnauthenticated()}, modifier = Modifier.fillMaxWidth()) {
                    Text("Retry Login")
                }
            }
            is AuthUiState.Authenticated -> {
                navController.navigate(Routes.campaignListScreen){
                    popUpTo(Routes.loginScreen){
                        inclusive = true
                    }
                }
            }
            else -> {
                Toast.makeText(context, "Some error occurred!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

