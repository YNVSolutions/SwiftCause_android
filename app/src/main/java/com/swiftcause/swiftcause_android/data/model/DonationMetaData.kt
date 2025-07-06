package com.swiftcause.swiftcause_android.data.model

data class DonationMetaData (
    val campaignId : String = "defaultCampaignId",
    val donorName : String = "defaultName",
    val donorId : String = "defaultId",
    val isGiftAid : Boolean = false,
    val platform : String = "android"
)