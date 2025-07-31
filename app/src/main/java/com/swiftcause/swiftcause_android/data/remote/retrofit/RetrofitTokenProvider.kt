package com.swiftcause.swiftcause_android.data.remote.retrofit

import com.stripe.stripeterminal.external.callable.ConnectionTokenCallback
import com.stripe.stripeterminal.external.callable.ConnectionTokenProvider
import com.stripe.stripeterminal.external.models.ConnectionTokenException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RetrofitTokenProvider(
    private val api: StripeApiService
) : ConnectionTokenProvider {

    override fun fetchConnectionToken(callback: ConnectionTokenCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getConnectionToken()
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!.secret)
                } else {
                    callback.onFailure(ConnectionTokenException("Failed to fetch token: ${response.code()}"))
                }
            } catch (e: Exception) {
                callback.onFailure(ConnectionTokenException("Exception: ${e.localizedMessage}"))
            }
        }
    }
}
