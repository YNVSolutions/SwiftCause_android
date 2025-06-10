package com.swiftcause.swiftcause_android.ui.shared

import com.swiftcause.swiftcause_android.data.model.Campaign

data class SharedUiState (
    val campaigns: List<Campaign> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)