package com.swiftcause.swiftcause_android

import android.app.Application
import com.stripe.android.PaymentConfiguration
import com.swiftcause.swiftcause_android.di.TerminalManagerEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject
import java.util.UUID

@HiltAndroidApp
class MyApplication : Application() {
    companion object {
        lateinit var sessionId: String
    }
    override fun onCreate() {
        super.onCreate()
        sessionId = UUID.randomUUID().toString()
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51RbHd9QKGKb5ypYFiKavxDZPvuYS2yQwSdXZ5Ul7EC74vZOoKnDvMfsLyfgXEpWxA51ozDTTV40OiiuK0STTmiyR00c1O0o9jh" // stripe publishable key
        )

        val terminalManager = EntryPointAccessors.fromApplication(
            this,
            TerminalManagerEntryPoint::class.java
        ).terminalManager()

        terminalManager.initialize()


    }
}

