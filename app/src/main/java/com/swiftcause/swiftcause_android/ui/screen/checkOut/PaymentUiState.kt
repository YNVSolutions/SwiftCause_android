package com.swiftcause.swiftcause_android.ui.screen.checkOut

sealed class PaymentUiState {
    object Idle : PaymentUiState()
    object Loading : PaymentUiState()
    data class ReadyForPayment(
        val clientSecret: String,
        val customerId: String,
        val ephemeralKeySecret: String,
        val publishableKey: String? // Pass publishableKey if present
    ) : PaymentUiState()
    data class Success(val message: String) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
    object Canceled : PaymentUiState()
}