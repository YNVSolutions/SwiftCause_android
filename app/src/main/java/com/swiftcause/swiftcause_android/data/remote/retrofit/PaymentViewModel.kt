package com.swiftcause.swiftcause_android.data.remote.retrofit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.swiftcause.swiftcause_android.data.model.mock.PaymentRequest
import com.swiftcause.swiftcause_android.data.model.mock.PaymentResponse
import com.swiftcause.swiftcause_android.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: PaymentRepository,
    @ApplicationContext private val applicationContext: Context
) : ViewModel() {

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

    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    fun initiatePayment(amount: Int, currency: String) {
        viewModelScope.launch {
            _uiState.value = PaymentUiState.Loading
            try {
//                val request = PaymentRequest(amount = amount, currency = currency)
                val response = repository.createPaymentIntent(amount)

                if (response?.paymentIntentClientSecret != null &&
                    response.customer != null &&
                    response.ephemeralKey != null
                ) {
                    // Initialize Stripe's PaymentConfiguration with the publishableKey
                    // if it's provided by your backend. Otherwise, it should be set
                    // once in your Application class.
                    response.publishableKey?.let {
                        PaymentConfiguration.init(applicationContext, it)
                    }

                    _uiState.value = PaymentUiState.ReadyForPayment(
                        clientSecret = response.paymentIntentClientSecret,
                        customerId = response.customer,
                        ephemeralKeySecret = response.ephemeralKey,
                        publishableKey = response.publishableKey // Pass this along
                    )
                } else {
                    // Handle cases where essential data is missing from the backend response
                    _uiState.value = PaymentUiState.Error(response?.status ?: "Failed") // Use status as error message
                }
            } catch (e: Exception) {
                // Network error, parsing error, etc.
                _uiState.value = PaymentUiState.Error("Payment initiation failed: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    fun handlePaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                _uiState.value = PaymentUiState.Canceled
            }
            is PaymentSheetResult.Failed -> {
                val error = paymentSheetResult.error
                _uiState.value = PaymentUiState.Error("Payment failed: ${error.localizedMessage ?: "Unknown error"}")
            }
            is PaymentSheetResult.Completed -> {
                _uiState.value = PaymentUiState.Success("Payment completed successfully!")
            }
        }
    }

    fun resetPaymentFlow() {
        _uiState.value = PaymentUiState.Idle
    }
}
