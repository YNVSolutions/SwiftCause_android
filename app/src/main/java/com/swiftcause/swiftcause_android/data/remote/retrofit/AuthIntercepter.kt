package com.swiftcause.swiftcause_android.data.remote.retrofit

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import jakarta.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking


class AuthInterceptor @Inject constructor(private val firebaseAuth: FirebaseAuth) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val tokenResult = runBlocking {
            firebaseAuth.currentUser?.getIdToken(false)?.await()
        }
        val token = tokenResult?.token
        Log.d("AuthInterceptor", "Firebase ID token: $token")

        val newRequest = if (token != null) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else original

        return chain.proceed(newRequest)
    }
}
