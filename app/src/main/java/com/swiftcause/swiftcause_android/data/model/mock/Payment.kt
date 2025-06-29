package com.swiftcause.swiftcause_android.data.model.mock

data class PaymentRequest(
    val amount: Int,
    val currency: String
)

data class PaymentResponse(
    val paymentIntentClientSecret: String?,
    val customer: String?,
    val ephemeralKey: String?,
    val status: String,
    val publishableKey: String? = null
)
