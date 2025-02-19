package com.example.mindbank.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mindbank.R
import com.example.mindbank.data.SaveData
import com.example.mindbank.data.Task
import com.example.mindbank.state.AdviceState
import com.example.mindbank.util.hexToColor
import com.example.mindbank.viewmodel.AdviceViewModel
import com.example.mindbank.viewmodel.TodoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    val isLoading = remember { mutableStateOf(true) }
    val itemList = remember { mutableStateListOf<Task>() }

    LaunchedEffect(key1 = refreshTrigger) {
        withContext(Dispatchers.IO) {
            val data = if (searchText.isNotEmpty()) viewModel.searchByKeyword(searchText)
            else viewModel.getAllData()
            itemList.clear()
            itemList.addAll(data)
        }
        isLoading.value = false // 로딩 상태 업데이트
    }

    val filteredList = if (searchText.isNotEmpty()) itemList.filter {
        it.title.contains(searchText, ignoreCase = true)
    } else itemList

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(filteredList) {
            ChecklistItem(item = it, onChecked = { todo ->
                viewModel.updateTodo(todo)
            }, onDelete = { todo ->
                viewModel.deleteTodo(todo.id)
            })
        }
    }
}

@Composable
fun ChecklistItem(
    item: Task,
    onChecked: (Task) -> Unit,
    onDelete: (Task) -> Unit // 삭제 이벤트 추가
) {
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
            Image(
                painter = painterResource(id = if (item.isDone) R.drawable.checked_img else R.drawable.unchecked_img),
                contentDescription = "Checkbox",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        item.isDone = !item.isDone
                        onChecked.invoke(item)
                    }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                modifier = Modifier
                    .size(20.dp)
                    .clickable { /* 편집 기능 추가 가능 */ }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete",
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onDelete.invoke(item) }
            )

            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}