package com.windrr.mindbank.presentation.navigation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.windrr.mindbank.state.AdviceState
import com.windrr.mindbank.viewmodel.AdviceViewModel
import com.windrr.mindbank.viewmodel.TodoViewModel

@Composable
fun AdviceScreen(adviceViewModel: AdviceViewModel, todoViewModel: TodoViewModel) {
    val uiState by adviceViewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 Advice 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // 상단 영역의 높이 조정
                .background(Color.LightGray), // 배경색 (선택)
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is AdviceState.Loading -> {
                    CircularProgressIndicator() // 로딩 상태
                }

                is AdviceState.Success -> {
                    Text(
                        text = (uiState as AdviceState.Success).advice,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    ) // 성공 상태 UI
                }

                is AdviceState.Error -> {
                    Text(
                        text = (uiState as AdviceState.Error).errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red
                    ) // 에러 상태 UI
                }
            }
        }
    }
}