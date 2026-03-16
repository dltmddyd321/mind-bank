package com.windrr.mindbank.presentation.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.windrr.mindbank.R
import com.windrr.mindbank.db.data.Memo
import com.windrr.mindbank.db.data.Task
import com.windrr.mindbank.presentation.ui.theme.SpaceBorder
import com.windrr.mindbank.presentation.ui.theme.SpaceLavender
import com.windrr.mindbank.presentation.ui.theme.SpacePurple
import com.windrr.mindbank.presentation.ui.theme.SpaceSurface
import com.windrr.mindbank.presentation.ui.theme.SpaceTheme
import com.windrr.mindbank.viewmodel.MemoViewModel
import com.windrr.mindbank.viewmodel.TodoViewModel
import kotlinx.coroutines.launch

/**
 * Wrapper composable that bridges ViewModel dependencies to the stable HomeScreen.
 * This maintains backward compatibility with existing callers.
 */
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    navController: NavController,
    memoViewModel: MemoViewModel,
    todoViewModel: TodoViewModel,
    onEditMemo: (Memo) -> Unit,
    onEditTodo: (Task) -> Unit
) {
    val memoList by memoViewModel.memos.collectAsState()
    val todoList by todoViewModel.todos.collectAsState()

    HomeScreenContent(
        memoList = memoList,
        todoList = todoList,
        onNavigateToNotes = {
            navController.navigate(Screen.Notes.route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        onNavigateToTodo = {
            navController.navigate(Screen.Todo.route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        onEditMemo = onEditMemo,
        onDeleteMemo = { memoViewModel.deleteData(it) },
        onEditTodo = onEditTodo,
        onUpdateTodo = { todoViewModel.updateTodo(it) },
        onDeleteTodo = { todoViewModel.deleteTodo(it) }
    )
}

/**
 * Stable HomeScreen composable with all stable parameters.
 * All parameters are @Stable: List<Memo>, List<Task>, and lambda functions.
 */
@ExperimentalMaterial3Api
@Composable
fun HomeScreenContent(
    memoList: List<Memo>,
    todoList: List<Task>,
    onNavigateToNotes: () -> Unit,
    onNavigateToTodo: () -> Unit,
    onEditMemo: (Memo) -> Unit,
    onDeleteMemo: (Memo) -> Unit,
    onEditTodo: (Task) -> Unit,
    onUpdateTodo: (Task) -> Unit,
    onDeleteTodo: (Int) -> Unit
) {
    SpaceTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                MainTopBar(stringResource(R.string.home_title))
            },
            content = { paddingValues ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
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
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = RoundedCornerShape(24.dp),
                                        ambientColor = SpacePurple.copy(alpha = 0.3f),
                                        spotColor = SpacePurple.copy(alpha = 0.3f)
                                    ),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = SpaceSurface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = BorderStroke(1.dp, SpaceBorder.copy(alpha = 0.3f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 12.dp)
                                                .clickable { onNavigateToNotes() },
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = stringResource(R.string.memo_title),
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )

                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_space_rocket),
                                                contentDescription = "Go to Memo Section",
                                                tint = Color.Unspecified,
                                                modifier = Modifier.size(20.dp)
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
                                                        onEdit = { onEditMemo(memo) },
                                                        onDelete = { onDeleteMemo(memo) }
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
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = RoundedCornerShape(24.dp),
                                        ambientColor = SpaceLavender.copy(alpha = 0.3f),
                                        spotColor = SpaceLavender.copy(alpha = 0.3f)
                                    ),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = SpaceSurface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = BorderStroke(1.dp, SpaceBorder.copy(alpha = 0.3f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 12.dp)
                                                .clickable { onNavigateToTodo() },
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = stringResource(R.string.todo_title),
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )

                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_space_alien),
                                                contentDescription = "Go to Todo Section",
                                                tint = Color.Unspecified,
                                                modifier = Modifier.size(20.dp)
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
                                                        onChecked = { onUpdateTodo(it) },
                                                        onEdit = { onEditTodo(task) },
                                                        onDelete = { onDeleteTodo(task.id) }
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