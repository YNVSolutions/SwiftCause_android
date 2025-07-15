package com.swiftcause.swiftcause_android.ui.shared


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftcause.swiftcause_android.data.model.Campaign
import com.swiftcause.swiftcause_android.data.repository.CampaignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: CampaignRepository
) : ViewModel() {


    private val _sharedUiState = MutableStateFlow(SharedUiState())
    val sharedUiState: StateFlow<SharedUiState> = _sharedUiState.asStateFlow()

//    var selectedCampaign : Campaign? = null;
    init {
        Log.i("Shared_vm", "Shared viewModel started")
        _sharedUiState.value = _sharedUiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                _sharedUiState.value = _sharedUiState.value.copy(
                    campaigns = repository.fetchCampaigns(),
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("Shared_vm", "Error fetching campaigns", e)
                _sharedUiState.value = _sharedUiState.value.copy(error = e.message)
            }
        }
    }

    fun setSelectedCampaign(campaign : Campaign?){
        _sharedUiState.value = _sharedUiState.value.copy(currentCampaign = campaign)
    }
    fun getSelectedCampaign() : Campaign?{
        return _sharedUiState.value.currentCampaign
    }
}