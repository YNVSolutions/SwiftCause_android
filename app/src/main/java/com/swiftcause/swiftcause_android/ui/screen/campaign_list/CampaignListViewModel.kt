package com.swiftcause.swiftcause_android.ui.screen.campaign_list

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftcause.swiftcause_android.data.model.mock.MockCampaignData
import com.swiftcause.swiftcause_android.ui.shared.SharedViewModel
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

//    init {
//        viewModelScope.launch {
//            loadMockCampaigns()
//        }
//
//    }

//    private fun loadMockCampaigns(){
//        _uiState.value = _uiState.value.copy(isLoading = true)
//        _uiState.value = _uiState.value.copy(
//            campaigns = MockCampaignData.campaigns,
//            isLoading = false
//        )
//    }

//    fun fetchCampaigns(viewModel : SharedViewModel){
//        Log.i("FirestoreTag", "Size of fetched campaigns: ${viewModel.campaigns.value.size}")
//        _uiState.value = _uiState.value.copy(isLoading = true)
//        _uiState.value = _uiState.value.copy(
//            campaigns = viewModel.campaigns.value,
//            isLoading = false
//        )
//    }

    fun observeShared(sharedViewModel : SharedViewModel){
//        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            sharedViewModel.sharedUiState.collect { sharedState ->
                _uiState.value = _uiState.value.copy(
                    isLoading = sharedState.isLoading,
                    campaigns = sharedState.campaigns,
                    error = sharedState.error
                )
            }
//            viewModel.campaigns.collect{ list ->
//                _uiState.value = _uiState.value.copy(
//                    campaigns = list,
//                    isLoading = false
//                )

//            }
        }
    }
}