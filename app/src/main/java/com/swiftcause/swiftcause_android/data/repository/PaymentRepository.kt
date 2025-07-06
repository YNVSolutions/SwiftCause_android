package com.swiftcause.swiftcause_android.data.repository

import com.swiftcause.swiftcause_android.data.model.DonationMetaData
import com.swiftcause.swiftcause_android.data.model.PaymentRequest
import com.swiftcause.swiftcause_android.data.model.PaymentResponse
import com.swiftcause.swiftcause_android.data.remote.retrofit.PaymentApi
import jakarta.inject.Inject

class PaymentRepository @Inject constructor(
    private val api: PaymentApi
) {
    suspend fun createPaymentIntent(amount: Int, currency: String = "gbp", metadata: DonationMetaData): PaymentResponse? {
        val request = PaymentRequest(amount = amount, currency = currency, metadata = metadata)
        val response = api.createPaymentIntent(request)
        return if (response.isSuccessful) response.body() else null
    }
}
