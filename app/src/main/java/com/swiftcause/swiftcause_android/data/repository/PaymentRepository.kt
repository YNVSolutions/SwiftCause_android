package com.swiftcause.swiftcause_android.data.repository

import com.swiftcause.swiftcause_android.data.model.mock.PaymentRequest
import com.swiftcause.swiftcause_android.data.model.mock.PaymentResponse
import com.swiftcause.swiftcause_android.data.remote.retrofit.PaymentApi
import com.swiftcause.swiftcause_android.data.remote.retrofit.RetrofitInstance
import jakarta.inject.Inject

class PaymentRepository @Inject constructor(
    private val api: PaymentApi
) {
    suspend fun createPaymentIntent(amount: Int): PaymentResponse? {
        val request = PaymentRequest(amount = amount, currency = "inr")
        val response = api.createPaymentIntent(request)
        return if (response.isSuccessful) response.body() else null
    }
}
