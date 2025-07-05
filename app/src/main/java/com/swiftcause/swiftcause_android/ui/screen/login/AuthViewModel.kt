package com.swiftcause.swiftcause_android.ui.screen.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth : FirebaseAuth
) : ViewModel() {

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Unauthenticated)
    val authUiState: StateFlow<AuthUiState> = _authUiState

    init {
        // Check if user is already signed in on app start
        auth.currentUser?.let { user ->
            _authUiState.value = AuthUiState.Authenticated(auth)
        }
    }

    fun launchFirebaseAuthUI(launcher: ActivityResultLauncher<Intent>) {
        _authUiState.value = AuthUiState.Authenticating

        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()

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
                _authUiState.value = AuthUiState.Authenticated(auth)

            } else {
                _authUiState.value = AuthUiState.Error("Authentication successful, but user is null.")
            }
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            val error = result?.error
            _authUiState.value = AuthUiState.Error(error?.message ?: "Sign-in cancelled or unknown error.")
        }
    }
    fun signOut(context : Context) {
        viewModelScope.launch {
            AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _authUiState.value = AuthUiState.Unauthenticated
                    } else {
                        _authUiState.value = AuthUiState.Error("Sign out failed: ${task.exception?.message}")
                    }
                }
        }
    }


}