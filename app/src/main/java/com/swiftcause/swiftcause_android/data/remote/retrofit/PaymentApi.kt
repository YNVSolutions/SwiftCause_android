package com.swiftcause.swiftcause_android.data.remote.retrofit


import com.swiftcause.swiftcause_android.data.model.PaymentRequest
import com.swiftcause.swiftcause_android.data.model.PaymentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApi {
    @POST("createPaymentIntent")
    suspend fun createPaymentIntent(
        @Body request: PaymentRequest
    ): Response<PaymentResponse>
}
