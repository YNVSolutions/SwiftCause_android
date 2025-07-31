package com.swiftcause.swiftcause_android.di

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.swiftcause.swiftcause_android.TerminalManager
import com.swiftcause.swiftcause_android.data.remote.retrofit.AuthInterceptor
import com.swiftcause.swiftcause_android.data.remote.retrofit.PaymentApi
import com.swiftcause.swiftcause_android.data.remote.retrofit.StripeApiService
import com.swiftcause.swiftcause_android.data.repository.CampaignRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideCampaignRepository(): CampaignRepository = CampaignRepository()

    @Provides
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://us-central1-swiftcause-app.cloudfunctions.net/createPaymentIntent/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun providePaymentApi(retrofit: Retrofit): PaymentApi {
        return retrofit.create(PaymentApi::class.java)
    }

    @Provides
    fun provideStripeTTPApi(retrofit: Retrofit): StripeApiService {
        return retrofit.create(StripeApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideTerminalManager(
        @ApplicationContext context: Context,
        stripeApiService: StripeApiService
    ): TerminalManager {
        return TerminalManager(context, stripeApiService)
    }

}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TerminalManagerEntryPoint {
    fun terminalManager(): TerminalManager
}
