package com.swiftcause.swiftcause_android.ui.screen.dummy_payment

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.swiftcause.swiftcause_android.data.remote.retrofit.PaymentViewModel
import com.swiftcause.swiftcause_android.ui.navigation.Routes
import androidx.compose.runtime.getValue
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.rememberPaymentSheet


@Composable
fun CheckOutScreen(
    campId: String,
    amount: String,
    navController: NavController,
    viewModel: PaymentViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // 1. Initialize PaymentSheet using rememberPaymentSheet
    // This handles the ActivityResultLauncher lifecycle for you
    val paymentSheet = rememberPaymentSheet(
        paymentResultCallback = viewModel::handlePaymentSheetResult
    )

    // 2. Observe the uiState and react when PaymentSheet should be presented
    LaunchedEffect(uiState) {
        when (uiState) {
            is PaymentViewModel.PaymentUiState.ReadyForPayment -> {
                val state = uiState as PaymentViewModel.PaymentUiState.ReadyForPayment
                val configuration = PaymentSheet.Configuration(
                    merchantDisplayName = "SwiftCause Inc.", // Your business name
                    customer = PaymentSheet.CustomerConfiguration(
                        state.customerId,
                        state.ephemeralKeySecret
                    ),
                    // Optional: You can pre-fill billing details if you have them
                    // defaultBillingDetails = PaymentSheet.BillingDetails(
                    //     email = "customer@example.com",
                    //     address = PaymentSheet.Address(country = "US")
                    // ),
                    allowsDelayedPaymentMethods = true // Enable deferred payments if desired
                )
                paymentSheet.presentWithPaymentIntent(
                    state.clientSecret,
                    configuration
                )
            }
            is PaymentViewModel.PaymentUiState.Success -> {
                Toast.makeText(context, (uiState as PaymentViewModel.PaymentUiState.Success).message, Toast.LENGTH_LONG).show()
                viewModel.resetPaymentFlow() // Reset UI state after success
            }
            is PaymentViewModel.PaymentUiState.Error -> {
                Toast.makeText(context, (uiState as PaymentViewModel.PaymentUiState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetPaymentFlow() // Reset UI state after error
            }
            is PaymentViewModel.PaymentUiState.Canceled -> {
                Toast.makeText(context, "Payment was canceled.", Toast.LENGTH_SHORT).show()
                viewModel.resetPaymentFlow() // Reset UI state after cancellation
            }
            else -> { /* Idle or Loading, no action needed here */ }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dummy stripe payment gateway screen...")
        Text("Paying £$amount to campId: $campId")
        Button(onClick = {
            // Call ViewModel to initiate payment (e.g., 50 GBP = 5000 pence)
            viewModel.initiatePayment(amount = 5000, currency = "gbp")
        },
            modifier = Modifier.fillMaxWidth(),
            // Disable button while loading or if PaymentSheet is about to be shown
            enabled = uiState is PaymentViewModel.PaymentUiState.Idle ||
                    uiState is PaymentViewModel.PaymentUiState.Success ||
                    uiState is PaymentViewModel.PaymentUiState.Error ||
                    uiState is PaymentViewModel.PaymentUiState.Canceled
        ) { Text("checkOut") }
    }

    when (uiState) {
        PaymentViewModel.PaymentUiState.Loading -> {
            CircularProgressIndicator()
            Text("Loading...")
        }
        is PaymentViewModel.PaymentUiState.ReadyForPayment -> {
            CircularProgressIndicator()
            Text("Waiting for Stripe UI...") // This is shown briefly before Stripe UI pops up
        }
        // For Success, Error, Canceled, the Toast handles immediate feedback,
        // and the UI resets back to Idle, so no persistent text is needed here
        else -> { /* Do nothing for Idle, Success, Error, Canceled */ }
    }
}
