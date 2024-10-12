package com.example.mindbank.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.mindbank.state.AdviceState
import com.example.mindbank.viewmodel.AdviceViewModel

@Composable
fun AdviceScreen(viewModel: AdviceViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is AdviceState.Loading -> {
            CircularProgressIndicator() // 로딩 상태 UI
        }
        is AdviceState.Success -> {
            Text(text = (uiState as AdviceState.Success).advice) // 성공 상태 UI
        }
        is AdviceState.Error -> {
            Text(text = (uiState as AdviceState.Error).errorMessage) // 에러 상태 UI
        }
    }
}