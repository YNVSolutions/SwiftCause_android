package com.swiftcause.swiftcause_android.data.model.mock

import com.swiftcause.swiftcause_android.data.model.Campaign
import com.swiftcause.swiftcause_android.data.model.CampaignStatus
import java.time.LocalDateTime
import java.time.ZonedDateTime

object MockCampaignData {
    val campaigns = listOf(
        Campaign(
            id = "123",
            title = "Plant 1000 Trees",
            description = "Join us to plant trees across the city.",
            goalAmount = 10000.0,
            collectedAmount = 4200.0,
            currency = "USD",
            status = CampaignStatus.ACTIVE,
            startDate = ZonedDateTime.now().minusDays(5),
            endDate = ZonedDateTime.now().plusDays(30),
            coverImageUrl = null, // You can add a mock URL here
            createdBy = "John Doe",
            tags = listOf("Environment", "Trees", "Green"),
            giftAidEnabled = true,
            donationCount = 42,
            lastUpdated = LocalDateTime.now()
        ),
        Campaign(
            id = "234",
            title = "Girls' Education Fund",
            description = "Support rural girls with education essentials.",
            goalAmount = 5000.0,
            collectedAmount = 5000.0,
            currency = "INR",
            status = CampaignStatus.COMPLETED,
            startDate = ZonedDateTime.now().minusMonths(1),
            endDate = ZonedDateTime.now().minusDays(5),
            coverImageUrl = null,
            createdBy = "NGO Trust",
            tags = listOf("Education", "Women"),
            giftAidEnabled = false,
            donationCount = 85,
            lastUpdated = LocalDateTime.now().minusDays(1)
        ),
        Campaign(
            id = "345",
            title = "COVID Relief",
            description = "Help families with essential supplies.",
            goalAmount = 20000.0,
            collectedAmount = 10000.0,
            currency = "USD",
            status = CampaignStatus.PAUSED,
            startDate = ZonedDateTime.now().minusWeeks(2),
            endDate = ZonedDateTime.now().plusWeeks(2),
            coverImageUrl = null,
            createdBy = "ReliefOrg",
            tags = listOf("Health", "Emergency"),
            giftAidEnabled = true,
            donationCount = 100,
            lastUpdated = LocalDateTime.now()
        ),
        Campaign(
            id = "456",
            title = "Plant 1000 Trees",
            description = "Join us to plant trees across the city.",
            goalAmount = 10000.0,
            collectedAmount = 4200.0,
            currency = "USD",
            status = CampaignStatus.ACTIVE,
            startDate = ZonedDateTime.now().minusDays(5),
            endDate = ZonedDateTime.now().plusDays(30),
            coverImageUrl = null, // You can add a mock URL here
            createdBy = "John Doe",
            tags = listOf("Environment", "Trees", "Green"),
            giftAidEnabled = true,
            donationCount = 42,
            lastUpdated = LocalDateTime.now()
        ),
        Campaign(
            id = "567",
            title = "COVID Relief",
            description = "Help families with essential supplies.",
            goalAmount = 20000.0,
            collectedAmount = 10000.0,
            currency = "USD",
            status = CampaignStatus.PAUSED,
            startDate = ZonedDateTime.now().minusWeeks(2),
            endDate = ZonedDateTime.now().plusWeeks(2),
            coverImageUrl = null,
            createdBy = "ReliefOrg",
            tags = listOf("Health", "Emergency"),
            giftAidEnabled = true,
            donationCount = 100,
            lastUpdated = LocalDateTime.now()
        ),
        Campaign(
            id = "678",
            title = "Girls' Education Fund",
            description = "Support rural girls with education essentials.",
            goalAmount = 5000.0,
            collectedAmount = 5000.0,
            currency = "INR",
            status = CampaignStatus.COMPLETED,
            startDate = ZonedDateTime.now().minusMonths(1),
            endDate = ZonedDateTime.now().minusDays(5),
            coverImageUrl = null,
            createdBy = "NGO Trust",
            tags = listOf("Education", "Women"),
            giftAidEnabled = false,
            donationCount = 85,
            lastUpdated = LocalDateTime.now().minusDays(1)
        ),Campaign(
            id = "789",
            title = "Girls' Education Fund",
            description = "Support rural girls with education essentials.",
            goalAmount = 5000.0,
            collectedAmount = 5000.0,
            currency = "INR",
            status = CampaignStatus.COMPLETED,
            startDate = ZonedDateTime.now().minusMonths(1),
            endDate = ZonedDateTime.now().minusDays(5),
            coverImageUrl = null,
            createdBy = "NGO Trust",
            tags = listOf("Education", "Women"),
            giftAidEnabled = false,
            donationCount = 85,
            lastUpdated = LocalDateTime.now().minusDays(1)
        ),
    )
}
