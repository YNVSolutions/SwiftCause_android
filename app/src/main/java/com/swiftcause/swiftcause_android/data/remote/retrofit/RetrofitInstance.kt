package com.swiftcause.swiftcause_android.data.remote.retrofit

import com.google.firebase.auth.FirebaseAuth
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://us-central1-swiftcause-app.cloudfunctions.net/createPaymentIntent"

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(FirebaseAuth.getInstance()))
        .build()

    val api: PaymentApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PaymentApi::class.java)
    }
}
