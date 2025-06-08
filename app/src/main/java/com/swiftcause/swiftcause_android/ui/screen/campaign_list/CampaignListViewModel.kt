package com.swiftcause.swiftcause_android.ui.screen.campaign_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftcause.swiftcause_android.data.model.mock.MockCampaignData
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CampaignListViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CampaignListUiState())
    val uiState : StateFlow<CampaignListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadMockCampaigns()
        }

    }

    private fun loadMockCampaigns(){
        _uiState.value = _uiState.value.copy(isLoading = true)
        _uiState.value = _uiState.value.copy(
            campaigns = MockCampaignData.campaigns,
            isLoading = false
        )
    }
}