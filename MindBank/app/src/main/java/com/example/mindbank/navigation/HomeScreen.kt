package com.example.mindbank.navigation

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mindbank.activity.AddTodoActivity
import com.example.mindbank.component.ChecklistItem
import com.example.mindbank.component.MemoItemView
import com.example.mindbank.data.SaveData
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
            val context = LocalContext.current
            val isLoading = remember { mutableStateOf(true) }
            val todoList = remember { mutableStateListOf<Task>() }
            val memoList = remember { mutableStateListOf<SaveData>() }

            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    memoList.clear()
                    memoList.addAll(dataViewModel.getAllData())
                    todoList.clear()
                    todoList.addAll(todoViewModel.getAllData())
                }
                isLoading.value = false
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(2.dp, Color.Gray.copy(alpha = 0.5f)),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(memoList.toList()) { memo ->
                                    MemoItemView(
                                        data = memo,
                                        onDelete = {
                                            dataViewModel.deleteData(memo)
                                            memoList.removeAll { it.id == memo.id }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(2.dp, Color.Gray.copy(alpha = 0.5f)),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(todoList.toList()) { task ->
                                ChecklistItem(
                                    item = task,
                                    onChecked = { todo ->
                                        val index = todoList.indexOfFirst { it.id == todo.id }
                                        if (index != -1) todoList[index] = todo
                                        todoViewModel.updateTodo(todo)
                                    },
                                    onEdit = { todo ->
                                        val intent = Intent(context, AddTodoActivity::class.java)
                                            .apply { putExtra("id", todo.id) }
                                        context.startActivity(intent)
                                    },
                                    onDelete = { todo ->
                                        todoList.removeAll { it.id == todo.id }
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
}