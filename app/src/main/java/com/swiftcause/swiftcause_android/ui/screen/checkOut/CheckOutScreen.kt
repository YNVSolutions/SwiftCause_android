package com.swiftcause.swiftcause_android.ui.screen.checkOut

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.swiftcause.swiftcause_android.ui.screen.checkOut.PaymentViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResultCallback

import com.swiftcause.swiftcause_android.ui.navigation.Routes
import com.swiftcause.swiftcause_android.ui.shared.SharedViewModel
import kotlin.math.roundToInt


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun CheckOutScreen(
    campId: String,
    amount: String,
    navController: NavController,
    viewModel: PaymentViewModel = hiltViewModel(),
) {
    val parentEntry = remember { navController.getBackStackEntry("campaignFlow") }
    val sharedViewModel: SharedViewModel = hiltViewModel(parentEntry)

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

    val enteredAmount = amount.toDoubleOrNull() ?: 0.0
    val pounds = enteredAmount * 100

    val initTapToPay by viewModel.initTapToPay.collectAsState()

    if (initTapToPay) {
        LocationPermissionHandler(
            onPermissionGranted = {
                viewModel.initTapToPayTerminal()
                viewModel.initTapToPay.value = false
            },
            onPermissionDenied = {
                viewModel.initTapToPay.value = false
            }
        )

    }


    LaunchedEffect(uiState) {
        if (checkoutButtonClicked && uiState is PaymentUiState.ReadyForPayment){
            val state = uiState as PaymentUiState.ReadyForPayment
            showStripeUI(
                clientSecret = state.clientSecret,
                uiState = state,
                paymentSheet = paymentSheet
            )
            checkoutButtonClicked = false;
        }
        when (uiState) {

            is PaymentUiState.Success -> {
                Toast.makeText(
                    context,
                    (uiState as PaymentUiState.Success).message,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetPaymentFlow()
                viewModel.resetPaymentFlow()
                navController.navigate(Routes.thankYouScreen) {
                    popUpTo(Routes.checkOutScreen) { // removing the checkout screen from the backstack
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
                Log.e("payment", "UiState says: " + (uiState as PaymentUiState.Error).message)
                viewModel.resetPaymentFlow() // Reset UI state after error
            }

            is PaymentUiState.Canceled -> {
                Toast.makeText(context, "Payment was canceled.", Toast.LENGTH_SHORT).show()
                viewModel.resetPaymentFlow() // Reset UI state after cancellation
            }

            else -> { // Idle or Loading, no action needed here
            }
        }
    }
    Scaffold(
        bottomBar = {
            Column {


                Button(
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp),
                    onClick = {
//                        checkoutButtonClicked = true
                        // check if the location permission is there and if not then request it.
                        viewModel.onTapToPayClicked()
                    },
//                    enabled = (clientSecret != null)
                ) {
                    Text("Pay with Tap to Pay")
                }
                Button(
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp),
                    onClick = {
                        checkoutButtonClicked = true
                        viewModel.initiatePayment(amount = pounds.roundToInt(), currency = "gbp", campId = campId)


                    },
                    enabled = uiState !is PaymentUiState.Loading && uiState !is PaymentUiState.ReadyForPayment
//                    enabled = (clientSecret != null)
                ) {
                    Text("Pay with card")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Checkout Screen", fontSize = MaterialTheme.typography.titleLarge.fontSize)
            Text("Donating £$amount to campaign: ")
            Text(
                "${sharedViewModel.getSelectedCampaign()?.title}",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            when (uiState) {
                is PaymentUiState.Loading -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.width(10.dp))
                        Text("Getting Ready for payment...")
                    }

                }

                is PaymentUiState.ReadyForPayment -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Ready for payment...")
                    }

                }
                // For Success, Error, Canceled, the Toast handles immediate feedback,
                // and the UI resets back to Idle, so no persistent text is needed here
                else -> { /* Do nothing for Idle, Success, Error, Canceled */
                }
            }



        }
    }
}
    fun showStripeUI(
        clientSecret: String?,
        uiState: PaymentUiState,
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

@Composable
fun LocationPermissionHandler(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    val permissionGranted = remember {
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
//            Toast.makeText(context, "Location permission is provided", Toast.LENGTH_LONG).show()
            onPermissionGranted()
        } else {
            Toast.makeText(context, "Location permission is required for Tap to Pay", Toast.LENGTH_LONG).show()
            onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        if (permissionGranted) {
//            Toast.makeText(context, "Location permission is provided", Toast.LENGTH_LONG).show()
            onPermissionGranted()
        } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}
