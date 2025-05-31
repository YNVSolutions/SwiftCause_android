package com.swiftcause.swiftcause_android.data.model

import java.time.ZonedDateTime

enum class PaymentStatus {
    SUCCESS,
    FAILED,
}

enum class DonationPlatform {
    WEB,
    MOBILE
}

data class Donation(
    val campaignId: String,
    val amount: Double,
    val currency: String = "INR",
    val donorId: String? = null,  // Nullable for anonymous donations
    val donorName: String? = null,
    val message: String? = null,
    val timestamp: ZonedDateTime? = null,
    val isGiftAid: Boolean = false,
    val paymentStatus: PaymentStatus = PaymentStatus.SUCCESS,
    val platform: DonationPlatform = DonationPlatform.WEB,
    val donationId: String? = null
)



/*
Sample data for reference
{
    "campaignId": "cmp_001",
    "amount": 1000,
    "currency": "INR",
    "donorId": "anon" or "user_abc123",
    "donorName": "Anonymous",
    "message": "Hope this helps!",
    "timestamp": "2025-04-21T09:00:00Z",
    "isGiftAid": true,
    "paymentStatus": "success",
    "platform": "web"
}
*/