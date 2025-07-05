package com.swiftcause.swiftcause_android.data.remote.retrofit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.swiftcause.swiftcause_android.data.repository.PaymentRepository
import com.swiftcause.swiftcause_android.ui.screen.checkOut.PaymentUiState
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

    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _clientSecretState = MutableStateFlow<String?>(null)
    val clientSecretState: StateFlow<String?> = _clientSecretState

    private var fetchedDetails = false;

    fun initiatePayment(amount: Int, currency: String) {
        if (!fetchedDetails) {
            viewModelScope.launch {
                _uiState.value = PaymentUiState.Loading
                try {
                    val response = repository.createPaymentIntent(amount, currency)

                    if (response?.paymentIntentClientSecret != null &&
                        response.customer != null &&
                        response.ephemeralKey != null
                    ) {
                        // Initialize Stripe's PaymentConfiguration with the publishableKey
                        // if it's provided by the backend. Otherwise, it should be set
                        // once in the Application class.
                        response.publishableKey?.let {
                            PaymentConfiguration.init(applicationContext, it)
                        }
                        _clientSecretState.value = response.paymentIntentClientSecret
                        _uiState.value = PaymentUiState.ReadyForPayment(
                            clientSecret = response.paymentIntentClientSecret,
                            customerId = response.customer,
                            ephemeralKeySecret = response.ephemeralKey,
                            publishableKey = response.publishableKey
                        )
                        fetchedDetails = true;
                    } else {
                        // missing data from the backend response
                        _uiState.value = PaymentUiState.Error(
                            response?.status ?: "Failed"
                        )
                    }
                } catch (e: Exception) {
                    // Network error, parsing error, etc.
                    _uiState.value =
                        PaymentUiState.Error("Payment initiation failed: ${e.localizedMessage ?: "Unknown error"}")
                }
            }
        }
    }

    fun handlePaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                _uiState.value = PaymentUiState.Canceled
                clearClientSecret()
            }
            is PaymentSheetResult.Failed -> {
                val error = paymentSheetResult.error
                _uiState.value = PaymentUiState.Error("Payment failed: ${error.localizedMessage ?: "Unknown error"}")
                clearClientSecret()
            }
            is PaymentSheetResult.Completed -> {
                _uiState.value = PaymentUiState.Success("Payment completed successfully!")
                clearClientSecret()

            }
        }
    }

    fun resetPaymentFlow() {
        _uiState.value = PaymentUiState.Idle
        clearClientSecret()
        resetFetchedDetailsFlag()
    }

    fun clearClientSecret(){
        _clientSecretState.value = null
    }

    fun resetFetchedDetailsFlag(){
        fetchedDetails = false;
    }
}
