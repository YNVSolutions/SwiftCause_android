package com.swiftcause.swiftcause_android.ui.screen.login

import com.google.firebase.auth.FirebaseAuth

sealed class AuthUiState {
    object Unauthenticated : AuthUiState()
    object Authenticating : AuthUiState()
    data class Authenticated(val user: FirebaseAuth) : AuthUiState()
    data class Error(val message: String?) : AuthUiState()
}
