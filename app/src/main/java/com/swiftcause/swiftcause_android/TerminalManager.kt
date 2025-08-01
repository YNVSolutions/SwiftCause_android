package com.swiftcause.swiftcause_android

import android.content.Context
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.TerminalListener
import com.stripe.stripeterminal.external.models.ConnectionStatus
import com.stripe.stripeterminal.log.LogLevel
import com.swiftcause.swiftcause_android.data.remote.logDebugStep
import com.swiftcause.swiftcause_android.data.remote.retrofit.RetrofitTokenProvider
import com.swiftcause.swiftcause_android.data.remote.retrofit.StripeApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class TerminalManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val stripeApiService: StripeApiService
) {

    fun initialize() {
        try {
            Terminal.initTerminal(
                context,
                LogLevel.VERBOSE,
                RetrofitTokenProvider(stripeApiService),
                object : TerminalListener {
                    override fun onConnectionStatusChange(status: ConnectionStatus) {
                        println("onConnectionStatusChange: $status")
                        logDebugStep("Terminal Listener has changes, status: $status")
                    }
                }
            )
            logDebugStep("Terminal initialized")
        }catch (e : Exception){
            logDebugStep("Could not initialize the terminal: ${e.message}")
        }

    }
}

