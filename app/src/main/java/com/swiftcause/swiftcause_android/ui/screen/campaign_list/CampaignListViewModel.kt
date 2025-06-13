package com.swiftcause.swiftcause_android.ui.screen.campaign_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftcause.swiftcause_android.ui.shared.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CampaignListViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CampaignListUiState())
    val uiState : StateFlow<CampaignListUiState> = _uiState.asStateFlow()

    fun getCampaignList(sharedViewModel : SharedViewModel){
        viewModelScope.launch {
            sharedViewModel.sharedUiState.collect { sharedState ->
                _uiState.value = _uiState.value.copy(
                    isLoading = sharedState.isLoading,
                    campaigns = sharedState.campaigns,
                    error = sharedState.error
                )
            }
        }
    }
}