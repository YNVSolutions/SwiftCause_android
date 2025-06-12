package com.swiftcause.swiftcause_android.ui.screen.campaign_details

import com.swiftcause.swiftcause_android.data.model.Campaign

data class CampaignDetailsUiState (
    val campaign: Campaign? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
