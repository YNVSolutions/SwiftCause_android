package com.swiftcause.swiftcause_android.ui.screen.checkOut

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.Callback
import com.stripe.stripeterminal.external.callable.Cancelable
import com.stripe.stripeterminal.external.callable.DiscoveryListener
import com.stripe.stripeterminal.external.callable.ReaderCallback
import com.stripe.stripeterminal.external.callable.TapToPayReaderListener
import com.stripe.stripeterminal.external.models.ConnectionConfiguration
import com.stripe.stripeterminal.external.models.DeviceType
import com.stripe.stripeterminal.external.models.DisconnectReason
import com.stripe.stripeterminal.external.models.DiscoveryConfiguration
import com.stripe.stripeterminal.external.models.Reader
import com.stripe.stripeterminal.external.models.TerminalException
import com.swiftcause.swiftcause_android.data.model.DonationMetaData
import com.swiftcause.swiftcause_android.data.remote.logDebugStep
import com.swiftcause.swiftcause_android.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID


@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: PaymentRepository,
    @ApplicationContext private val applicationContext: Context
) : ViewModel() {

    private val BUSINESS_LOCATION_ID = "tml_GIgICwZfMOuFTJ"
    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _clientSecretState = MutableStateFlow<String?>(null)
    val clientSecretState: StateFlow<String?> = _clientSecretState

    private val _tapToPayState = MutableStateFlow<TapToPayState?>(null)
    val tapToPayState: StateFlow<TapToPayState?> = _tapToPayState.asStateFlow()


    private var fetchedDetails = false
    val initTapToPay = MutableStateFlow(false)

    private var discoveryCancelable: Cancelable? = null


    fun onTapToPayClicked() {
        initTapToPay.value = true
    }
//    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun initTapToPayTerminal() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            _tapToPayState.value = TapToPayState.Error("Location permissions not granted")
            Log.d("tapToPay", "Location permission missing")
            logDebugStep("Location permission missing")
            return
        }
        _tapToPayState.value = TapToPayState.Initializing

        val config = DiscoveryConfiguration.TapToPayDiscoveryConfiguration(isSimulated = true)
    Log.d("tapToPay", "about to start discovery")
    logDebugStep("About to start reader discovery")
        discoveryCancelable = Terminal.getInstance().discoverReaders(
            config,
            object : DiscoveryListener {
                override fun onUpdateDiscoveredReaders(readers: List<Reader>) {
                    val tapToPayReaders = readers.filter { it.deviceType == DeviceType.TAP_TO_PAY_DEVICE }
                    logDebugStep("Discovered ${readers.size} readers")
                    logDebugStep("Found ${tapToPayReaders.size} TTP readers")

                    if (tapToPayReaders.isNotEmpty() && Terminal.getInstance().connectedReader == null) {
                        connectToTapToPayReader(tapToPayReaders.first())
                    }
                }
            },
            object : Callback {
                override fun onSuccess() {
                    Log.d("TapToPay", "Discovery started successfully")
                    logDebugStep("Started reader discovery")
                }

                override fun onFailure(e: TerminalException) {
                    Log.d("tapToPay", "this is discovery onfailure, ${e.errorMessage}")
                    logDebugStep("Discovery failed: ${e.errorMessage}")
                    _tapToPayState.value = TapToPayState.Error(e.errorMessage)
                }
            }
        )
    }
    private fun connectToTapToPayReader(reader: Reader) {
        val connectionConfig = ConnectionConfiguration.TapToPayConnectionConfiguration(
            locationId = BUSINESS_LOCATION_ID,
            autoReconnectOnUnexpectedDisconnect = true,
            tapToPayReaderListener = object : TapToPayReaderListener {
                override fun onDisconnect(reason: DisconnectReason) {
                    Log.d("TapToPay", "Reader disconnected: ${reason.name}")
                    logDebugStep("Reader disconnected: ${reason.name}")
                    _tapToPayState.value = TapToPayState.Disconnected(reason.name)
                }

                override fun onReaderReconnectStarted(
                    reader: Reader,
                    cancelReconnect: Cancelable,
                    reason: DisconnectReason
                ) {
                    Log.d("TapToPay", "Reconnection started due to: ${reason.name}")
                    logDebugStep("Reconnection started due to: ${reason.name}")
                    _tapToPayState.value = TapToPayState.Reconnecting
                    // You can store cancelReconnect if you want to cancel reconnection later
                }

                override fun onReaderReconnectSucceeded(reader: Reader) {
                    Log.d("TapToPay", "Reconnection succeeded")
                    logDebugStep("Reconnection succeeded")
                    _tapToPayState.value = TapToPayState.Ready
                }

                override fun onReaderReconnectFailed(reader: Reader) {
                    Log.e("TapToPay", "Reconnection failed")
                    logDebugStep("Reconnection failed")
                    _tapToPayState.value = TapToPayState.Error("Reconnection failed")
                }
            }
        )

        Terminal.getInstance().connectReader(
            reader,
            connectionConfig,
            object : ReaderCallback {
                override fun onSuccess(connectedReader: Reader) {
                    Log.d("TapToPay", "Connected to tap to pay reader")
                    logDebugStep("Connected to a reader!!")
                    _tapToPayState.value = TapToPayState.Ready
                    cancelDiscovery()
                }

                override fun onFailure(e: TerminalException) {
                    Log.e("TapToPay", "Failed to connect: ${e.errorMessage}")
                    logDebugStep("Failed to connect: ${e.errorMessage}")
                    _tapToPayState.value = TapToPayState.Error(e.errorMessage)
                }
            }
        )
    }
    private fun cancelDiscovery() {
        discoveryCancelable?.cancel(object : Callback {
            override fun onSuccess() {
                Log.d("TapToPay", "Discovery cancelled successfully")
                logDebugStep("Discovery cancelled successfully")
                discoveryCancelable = null
            }
            override fun onFailure(e: TerminalException) {
                Log.e("TapToPay", "Failed to cancel discovery: ${e.errorMessage}")
                logDebugStep("Failed to cancel discovery: ${e.errorMessage}")
            }
        })
    }

    fun initiatePayment(amount: Int, currency: String, campId: String) {
        if (!fetchedDetails) {
            viewModelScope.launch {
                _uiState.value = PaymentUiState.Loading
                try {
                    val metadata = DonationMetaData(
                        campaignId = campId,
                        donorName = "TestUser",
                        donorId = "TestUid"
                    )
                    val response = repository.createPaymentIntent(amount, currency, metadata)

                    if (response?.paymentIntentClientSecret != null &&
                        response.customer != null &&
                        response.ephemeralKey != null
                    ) {
                        // Initialize Stripe's PaymentConfiguration with the publishableKey
                        // if it's provided by the backend. Otherwise, it should be set
                        // once in the Application class.
                        response.publishableKey?.let {
                            PaymentConfiguration.Companion.init(applicationContext, it)
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
sealed class TapToPayState {
    object Initializing : TapToPayState()
    object Ready : TapToPayState()
    object Reconnecting : TapToPayState()
    data class Disconnected(val reason: String) : TapToPayState()
    data class Error(val message: String) : TapToPayState()
}