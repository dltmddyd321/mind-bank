package com.windrr.mindbank.presentation.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.windrr.mindbank.R
import com.windrr.mindbank.db.data.Memo
import com.windrr.mindbank.db.data.Task
import com.windrr.mindbank.presentation.ui.theme.MindBankTheme
import com.windrr.mindbank.viewmodel.MemoViewModel
import com.windrr.mindbank.viewmodel.TodoViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    navController: NavController,
    memoViewModel: MemoViewModel,
    todoViewModel: TodoViewModel,
    paddingValues: PaddingValues,
    onEditMemo: (Memo) -> Unit,
    onEditTodo: (Task) -> Unit
) {
    MindBankTheme {
        Scaffold(
            modifier = Modifier.padding(paddingValues),
            topBar = {
                MainTopBar(stringResource(R.string.home_title))
            },
            content = {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    val coroutineScope = rememberCoroutineScope()
                    var selectedMemo by remember { mutableStateOf<Memo?>(null) }

                    selectedMemo?.let { memo ->
                        MemoSheet(memo, sheetState) {
                            selectedMemo = null
                            coroutineScope.launch { sheetState.hide() }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            val memoList by memoViewModel.memos.collectAsState()

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(2.dp, Color.Gray.copy(alpha = 0.5f)),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 4.dp, vertical = 8.dp)
                                                .clickable {
                                                    navController.navigate(Screen.Notes.route) {
                                                        popUpTo(navController.graph.startDestinationId) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                },
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = stringResource(R.string.memo_title),
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )

                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_chevron_right_24),
                                                contentDescription = "Go to Memo Section",
                                                tint = MaterialTheme.colorScheme.onBackground
                                            )
                                        }

                                        Divider(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                            thickness = 1.dp,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        if (memoList.isNotEmpty()) {
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                memoList.forEach { memo ->
                                                    MemoItemView(
                                                        data = memo,
                                                        onClick = {
                                                            selectedMemo = memo
                                                            coroutineScope.launch { sheetState.show() }
                                                        },
                                                        onEdit = { onEditMemo.invoke(memo) },
                                                        onDelete = { memoViewModel.deleteData(memo) }
                                                    )
                                                }
                                            }
                                        } else {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 32.dp)
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.empty_memo),
                                                    textAlign = TextAlign.Center,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            val todoList by todoViewModel.todos.collectAsState()

                            val list = remember { mutableStateListOf<Task>() }
                            LaunchedEffect(todoList) {
                                list.clear()
                                list.addAll(todoList)
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(2.dp, Color.Gray.copy(alpha = 0.5f)),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 4.dp, vertical = 8.dp)
                                                .clickable {
                                                    navController.navigate(Screen.Todo.route) {
                                                        popUpTo(navController.graph.startDestinationId) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                },
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = stringResource(R.string.todo_title),
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )

                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_chevron_right_24),
                                                contentDescription = "Go to Memo Section",
                                                tint = MaterialTheme.colorScheme.onBackground
                                            )
                                        }

                                        Divider(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                            thickness = 1.dp,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        if (todoList.isNotEmpty()) {
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                todoList.forEach { task ->
                                                    ChecklistItem(
                                                        item = task,
                                                        onChecked = { checkedTodo -> todoViewModel.updateTodo(checkedTodo) },
                                                        onEdit = { onEditTodo.invoke(task) },
                                                        onDelete = { todoViewModel.deleteTodo(task.id) }
                                                    )
                                                }
                                            }
                                        } else {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 32.dp)
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.empty_todo),
                                                    textAlign = TextAlign.Center,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
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