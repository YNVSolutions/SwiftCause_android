package com.swiftcause.swiftcause_android.ui.screen.campaign_list

import com.swiftcause.swiftcause_android.data.model.Campaign

data class CampaignListUiState(
    val campaigns: List<Campaign> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
