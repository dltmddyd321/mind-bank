package com.example.mindbank.navigation

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mindbank.R
import com.example.mindbank.component.ChecklistItem
import com.example.mindbank.component.MemoItemView
import com.example.mindbank.data.Memo
import com.example.mindbank.data.Task
import com.example.mindbank.ui.theme.MindBankTheme
import com.example.mindbank.viewmodel.MemoViewModel
import com.example.mindbank.viewmodel.TodoViewModel

@Composable
fun HomeScreen(
    memoViewModel: MemoViewModel,
    todoViewModel: TodoViewModel,
    paddingValues: PaddingValues,
    onEditMemo: (Memo) -> Unit,
    onEditTodo: (Task) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        MindBankTheme {
            Scaffold(
                topBar = {
                    MainTopBar("Home")
                },
                content = {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val todoList by todoViewModel.todos.collectAsState()
                        val memoList by memoViewModel.memos.collectAsState()

                        Box(
                            modifier = Modifier.fillMaxSize(),
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
                                        if (memoList.isNotEmpty()) {
                                            LazyColumn(
                                                modifier = Modifier.fillMaxSize(),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                items(memoList.toList()) { memo ->
                                                    MemoItemView(data = memo, onEdit = {
                                                        onEditMemo.invoke(memo)
                                                    }, onDelete = {
                                                        memoViewModel.deleteData(memo)
                                                    })
                                                }
                                            }
                                        } else {
                                            Box(
                                                contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
                                            ) {
                                                Text(text = stringResource(R.string.empty_memo), textAlign = TextAlign.Center)
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
                                    if (todoList.isNotEmpty()) {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            items(todoList.toList()) { task ->
                                                ChecklistItem(item = task, onChecked = {
                                                    todoViewModel.updateTodo(task)
                                                }, onEdit = {
                                                    onEditTodo.invoke(task)
                                                }, onDelete = {
                                                    todoViewModel.deleteTodo(task.id)
                                                })
                                            }
                                        }
                                    } else {
                                        Box(
                                            contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(text = stringResource(R.string.empty_todo), textAlign = TextAlign.Center)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}