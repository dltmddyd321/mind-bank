package com.example.mindbank.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mindbank.data.Task
import com.example.mindbank.state.AdviceState
import com.example.mindbank.viewmodel.AdviceViewModel
import com.example.mindbank.viewmodel.TodoViewModel

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


@Composable
fun ChecklistList(viewModel: TodoViewModel, searchText: String, refreshTrigger: Boolean) {
    val checklistItems = remember { mutableStateOf<List<Task>>(emptyList()) }

    // 비동기로 데이터 가져오기
    LaunchedEffect(Unit) {
        checklistItems.value = viewModel.getAllData()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(checklistItems.value) {
            ChecklistItem(item = it) { todo ->
                viewModel.updateTodo(todo)
            }
        }
    }
}

@Composable
fun ChecklistItem(item: Task, onChecked: (Task) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(Color.Transparent),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 체크박스
            Checkbox(
                checked = item.isDone,
                onCheckedChange = {
                    item.isDone = it
                    onChecked.invoke(item)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))

            // 텍스트가 남은 공간을 모두 차지하도록 weight(1f) 추가
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f) // 자동 줄바꿈 & 여백 조정
            )

            // 오른쪽 끝 아이콘을 유지하기 위한 Spacer 추가
            Spacer(modifier = Modifier.width(8.dp))

            // 기존 Edit 아이콘 유지
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                modifier = Modifier.size(20.dp) // 아이콘 크기 조정
            )
        }
    }
}