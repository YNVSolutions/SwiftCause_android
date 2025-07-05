package com.swiftcause.swiftcause_android

import android.app.Application
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51RbHd9QKGKb5ypYFiKavxDZPvuYS2yQwSdXZ5Ul7EC74vZOoKnDvMfsLyfgXEpWxA51ozDTTV40OiiuK0STTmiyR00c1O0o9jh" // ✅ Your publishable key here
        )
    }
}