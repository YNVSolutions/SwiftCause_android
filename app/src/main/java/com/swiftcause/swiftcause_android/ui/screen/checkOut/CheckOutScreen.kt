package com.swiftcause.swiftcause_android.ui.screen.checkOut

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResultCallback
import com.swiftcause.swiftcause_android.ui.navigation.Routes
import kotlin.math.roundToInt


@Composable
fun CheckOutScreen(
    campId: String,
    amount: String,
    navController: NavController,
    viewModel: PaymentViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val clientSecret by viewModel.clientSecretState.collectAsState()

    var checkoutButtonClicked by remember { mutableStateOf(false) }
    val paymentResultCallback = remember {
        PaymentSheetResultCallback { paymentResult ->
            viewModel.handlePaymentSheetResult(paymentResult)
        }
    }


    val paymentSheet = remember(paymentResultCallback) {
        PaymentSheet.Builder(paymentResultCallback)
    }.build()

    val enteredAmount = amount.toDoubleOrNull() ?: 0.0;
    val pounds = enteredAmount * 100;
    LaunchedEffect(Unit) {
        viewModel.initiatePayment(amount = pounds.roundToInt(), currency = "gbp")
    }



    LaunchedEffect(uiState) {
        when (uiState) {

            is PaymentUiState.Success -> {
                Toast.makeText(
                    context,
                    (uiState as PaymentUiState.Success).message,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetPaymentFlow()
                viewModel.resetPaymentFlow()
                navController.navigate(Routes.thankYouScreen){
                    popUpTo(Routes.checkOutScreen){ // removing the checkout screen from the backstack
                        inclusive = true
                    }
                }
            }

            is PaymentUiState.Error -> {
                Toast.makeText(
                    context,
                    (uiState as PaymentUiState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                Log.e("payment", (uiState as PaymentUiState.Error).message)
                viewModel.resetPaymentFlow() // Reset UI state after error
            }

            is PaymentUiState.Canceled -> {
                Toast.makeText(context, "Payment was canceled.", Toast.LENGTH_SHORT).show()
                viewModel.resetPaymentFlow() // Reset UI state after cancellation
            }

            else -> { /* Idle or Loading, no action needed here */
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                checkoutButtonClicked = true
                showStripeUI(
                    clientSecret = clientSecret,
                    uiState = uiState,
                    viewModel = viewModel,
                    paymentSheet = paymentSheet
                );

            },
            enabled = (clientSecret != null)
        ) {
            Text("Proceed to payment")
        }
        Text("Checkout Screen...")
        Text("Donating £$amount to campId: $campId")

        when (uiState) {
            is PaymentUiState.Loading -> {
                CircularProgressIndicator()
                Text("Loading...")
            }

            is PaymentUiState.ReadyForPayment -> {
//                CircularProgressIndicator()
                Text("Initializing payment...")
            }
            // For Success, Error, Canceled, the Toast handles immediate feedback,
            // and the UI resets back to Idle, so no persistent text is needed here
            else -> { /* Do nothing for Idle, Success, Error, Canceled */
            }
        }

    }
}

fun showStripeUI(
    clientSecret: String?,
    uiState: PaymentUiState,
    viewModel: PaymentViewModel,
    paymentSheet: PaymentSheet
) {
    if (clientSecret != null) {
        val state = uiState as? PaymentUiState.ReadyForPayment
        state?.let {
            val configuration = PaymentSheet.Configuration(
                merchantDisplayName = "SwiftCause",
                customer = PaymentSheet.CustomerConfiguration(
                    state.customerId,
                    state.ephemeralKeySecret
                ),

                allowsDelayedPaymentMethods = false // Enable deferred payments
            )

            paymentSheet.presentWithPaymentIntent(
                state.clientSecret,
                configuration
            )
        }
    }
}