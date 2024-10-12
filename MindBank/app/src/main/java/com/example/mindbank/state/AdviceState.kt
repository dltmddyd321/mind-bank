package com.example.mindbank.state

sealed class AdviceState {
    object Loading : AdviceState()
    data class Success(val advice: String) : AdviceState()
    data class Error(val errorMessage: String) : AdviceState()
}