package com.example.mindbank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindbank.api.ApiService
import com.example.mindbank.api.NetworkModule
import com.example.mindbank.state.AdviceIntent
import com.example.mindbank.state.AdviceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdviceViewModel @Inject constructor(
    @NetworkModule.Advice private val apiService: ApiService
) : ViewModel() {
    private val _uiState = MutableStateFlow<AdviceState>(AdviceState.Loading)
    val uiState: StateFlow<AdviceState> = _uiState

    init {
        callIntent(AdviceIntent.FetchAdvice)
    }

    private fun callIntent(intent: AdviceIntent) {
        when (intent) {
            is AdviceIntent.FetchAdvice -> fetchAdvice()
        }
    }

    private fun fetchAdvice() {
        viewModelScope.launch {
            _uiState.value = AdviceState.Loading
            try {
                val response = apiService.fetchAdvice()
                if (response.isSuccessful) {
                    val advice = response.body()?.slip?.advice ?: "No advice found"
                    _uiState.value = AdviceState.Success(advice)
                } else {
                    _uiState.value = AdviceState.Error("Failed to fetch advice")
                }
            } catch (e: Exception) {
                _uiState.value = AdviceState.Error(e.message ?: "Unknown error")
            }
        }
    }
}