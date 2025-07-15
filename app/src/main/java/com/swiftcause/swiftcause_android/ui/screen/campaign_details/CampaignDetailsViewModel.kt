package com.swiftcause.swiftcause_android.ui.screen.campaign_details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftcause.swiftcause_android.ui.shared.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class CampaignDetailsViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(CampaignDetailsUiState())
    val uiState : StateFlow<CampaignDetailsUiState> = _uiState.asStateFlow()

    fun getCampaignDetails(campId : String, sharedViewModel: SharedViewModel){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val sharedState = sharedViewModel.sharedUiState
                .filter { it.campaigns.isNotEmpty() }
                .first()

            val campaign = sharedState.campaigns.find { it.id == campId }
            if (campaign != null){
                sharedViewModel.setSelectedCampaign(campaign)
                _uiState.value = _uiState.value.copy(campaign = campaign, isLoading = false)
            }else{
                _uiState.value = _uiState.value.copy(error = "Could not find the requested campaign", isLoading = false)
            }
        }
    }

}