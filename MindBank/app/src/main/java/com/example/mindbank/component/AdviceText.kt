package com.example.mindbank.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
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
import com.example.mindbank.state.AdviceState
import com.example.mindbank.viewmodel.AdviceViewModel

@Composable
fun AdviceScreen(viewModel: AdviceViewModel) {
    val uiState by viewModel.uiState.collectAsState()

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

        // 하단 체크리스트
        ChecklistList(viewModel = viewModel)
    }
}


@Composable
fun ChecklistList(viewModel: AdviceViewModel) {
//    val checklistItems = viewModel.checklistItems.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f) // 남은 공간을 모두 사용
            .padding(16.dp)
    ) {
        items(checklistItems.value) { item ->
            ChecklistItem(item)
        }
    }
}

@Composable
fun ChecklistItem(item: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = false, // 체크 여부 (동적으로 설정 가능)
            onCheckedChange = { /* Update state */ }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = item, style = MaterialTheme.typography.bodyMedium)
    }
}