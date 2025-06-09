package com.swiftcause.swiftcause_android.data.model

import java.time.LocalDateTime
import java.time.ZonedDateTime

enum class CampaignStatus {
    active, complete, paused  // should be all caps but as the data in firebase currently has lowercase, using this for testing
}

data class Campaign(
    // id set to zero temporarily for testing
    val id: String = "000",  // to be mapped explicitly as it is the auto generated key or id of the firestore document
    val title: String = "This is a mock title",
    val description: String? = null,
    val goalAmount: Double = 0.0,
    val collectedAmount: Double = 0.0,
    val currency: String = "USD",
    val status: String = "active", // use enum later
    val startDate: String? = null,
    val endDate: String? = null,
    val coverImageUrl: String? = null,
    val createdBy: String? = null,
    val tags: List<String> = emptyList(),
    val giftAidEnabled: Boolean = false,
    val donationCount: Int = 0,
    val lastUpdated: String? = null
)
/*
Sample data for reference

"title": "Clean Water for Kids",
  "description": "Providing access to clean drinking water...",
  "goalAmount": 50000,
  "collectedAmount": 12500,
  "currency": "INR",
  "status": "active", // other options: "completed", "paused"
  "startDate": "2025-04-01T00:00:00Z",
  "endDate": "2025-06-30T23:59:59Z",
  "coverImageUrl": "https://...",
  "createdBy": "admin_uid_01",
  "tags": ["children", "water", "health"],
  "giftAidEnabled": true,
  "donationCount": 44,
  "lastUpdated": "2025-04-20T18:00:00Z"

 */