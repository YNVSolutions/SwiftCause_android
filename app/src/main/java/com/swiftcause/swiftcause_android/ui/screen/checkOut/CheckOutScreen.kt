package com.swiftcause.swiftcause_android.ui.screen.checkOut

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.swiftcause.swiftcause_android.data.remote.retrofit.PaymentViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResultCallback
import com.stripe.android.paymentsheet.rememberPaymentSheet
import com.swiftcause.swiftcause_android.ui.navigation.Routes


@Composable
fun CheckOutScreen(
    campId: String,
    amount: String,
    navController: NavController,
    viewModel: PaymentViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()


    val paymentResultCallback = remember {
        PaymentSheetResultCallback { paymentResult ->
            viewModel.handlePaymentSheetResult(paymentResult)
        }
    }


    val paymentSheet = remember(paymentResultCallback) {
        PaymentSheet.Builder(paymentResultCallback)
    }.build()


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
                navController.navigate(Routes.thankYouScreen)
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
        modifier = Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dummy stripe payment gateway screen...")
        Text("Paying £$amount to campId: $campId")
        Button(onClick = {
            // Call ViewModel to initiate payment (e.g., 50 GBP = 5000 pence)
            val pounds = amount.toIntOrNull() ?: 0
            viewModel.initiatePayment(amount = pounds * 100, currency = "gbp")
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
