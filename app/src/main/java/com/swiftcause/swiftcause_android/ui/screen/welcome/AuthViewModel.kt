package com.swiftcause.swiftcause_android.ui.screen.welcome

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.swiftcause.swiftcause_android.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Unauthenticated : AuthState()
    object Authenticating : AuthState()
    data class Authenticated(val user: FirebaseAuth) : AuthState()
    data class Error(val message: String?) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        // Check if user is already signed in on app start
        auth.currentUser?.let { user ->
            _authState.value = AuthState.Authenticated(auth)
        }
    }

    fun launchFirebaseAuthUI(launcher: ActivityResultLauncher<Intent>) {
        _authState.value = AuthState.Authenticating

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers).setCredentialManagerEnabled(false)
            .build()

        launcher.launch(signInIntent)
    }

    fun handleAuthResult(result: IdpResponse?, resultCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            // Successfully signed in
            val user = auth.currentUser
            if (user != null) {
                _authState.value = AuthState.Authenticated(auth)

            } else {
                _authState.value = AuthState.Error("Authentication successful, but user is null.")
            }
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            val error = result?.error
            _authState.value = AuthState.Error(error?.message ?: "Sign-in cancelled or unknown error.")
        }
    }
    fun signOut(context : Context) {
        viewModelScope.launch {
            // Get application context from AndroidViewModel
            AuthUI.getInstance()
                .signOut(context) // Correct way to sign out
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _authState.value = AuthState.Unauthenticated
                    } else {
                        _authState.value = AuthState.Error("Sign out failed: ${task.exception?.message}")
                    }
                }
        }
    }


}