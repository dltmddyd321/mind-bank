package com.example.mindbank.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mindbank.component.ChecklistItem
import com.example.mindbank.data.Task
import com.example.mindbank.ui.theme.MindBankTheme
import com.example.mindbank.viewmodel.DataViewModel
import com.example.mindbank.viewmodel.TodoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    dataViewModel: DataViewModel,
    todoViewModel: TodoViewModel,
    paddingValues: PaddingValues
) {
    MindBankTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val isLoading = remember { mutableStateOf(true) }
            val itemList = remember { mutableStateListOf<Task>() }

            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    val data = todoViewModel.getAllData()
                    itemList.clear()
                    itemList.addAll(data)
                }
                isLoading.value = false
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, Color.Gray.copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(itemList.toList()) { task ->
                            ChecklistItem(
                                item = task,
                                onChecked = { todo ->
                                    val index = itemList.indexOfFirst { it.id == todo.id }
                                    if (index != -1) itemList[index] = todo
                                    todoViewModel.updateTodo(todo)
                                },
                                onEdit = {
                                    //TODO : 편집 클릭 시 처리
                                },
                                onDelete = { todo ->
                                    itemList.removeAll { it.id == todo.id }
                                    todoViewModel.deleteTodo(todo.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}