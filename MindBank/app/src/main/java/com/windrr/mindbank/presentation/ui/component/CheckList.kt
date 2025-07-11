package com.windrr.mindbank.presentation.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.windrr.mindbank.R
import com.windrr.mindbank.db.data.Task
import com.windrr.mindbank.util.dragContainer
import com.windrr.mindbank.util.draggableItems
import com.windrr.mindbank.util.hexToColor
import com.windrr.mindbank.util.rememberDragDropState
import com.windrr.mindbank.viewmodel.TodoViewModel

@Composable
fun ChecklistList(viewModel: TodoViewModel, searchText: String, onEdit: (Task) -> Unit) {
    val itemList by viewModel.todos.collectAsState()
    val initList = if (searchText.isNotEmpty()) {
        itemList.filter { it.title.contains(searchText, ignoreCase = true) }
    } else itemList
    var filteredList = remember { mutableStateListOf<Task>() }
    LaunchedEffect(itemList, searchText) {
        filteredList.clear()
        filteredList.addAll(initList.sortedByDescending { it.position })
    }
    val stateList = rememberLazyListState()
    val draggableItems by remember { derivedStateOf { filteredList.size } }
    val dragDropState =
        rememberDragDropState(
            lazyListState = stateList,
            draggableItemsNum = draggableItems,
            onMove = { fromIndex, toIndex ->
                filteredList = filteredList.apply { add(toIndex, removeAt(fromIndex)) }
            }, onDragEnd = {
                val now = System.currentTimeMillis()
                filteredList.forEachIndexed { index, task ->
                    task.position = now - index
                    viewModel.updateTodo(task)
                }
            })


    if (filteredList.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .dragContainer(dragDropState)
                .fillMaxSize()
                .padding(16.dp),
            state = stateList
        ) {
            draggableItems(filteredList, dragDropState = dragDropState) { modifier, item ->
                ChecklistItem(item = item, modifier = modifier, onChecked = { todo ->
                    viewModel.updateTodo(todo)
                }, onEdit = { todo ->
                    onEdit.invoke(todo)
                }, onDelete = { todo -> viewModel.deleteTodo(todo.id) })
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

@Composable
fun ChecklistItem(
    item: Task,
    modifier: Modifier = Modifier,
    onChecked: (Task) -> Unit,
    onEdit: (Task) -> Unit,
    onDelete: (Task) -> Unit
) {
    Card(
        modifier = modifier
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
            var showDialog by remember { mutableStateOf(false) }

            Image(
                painter = painterResource(id = if (item.isDone) R.drawable.checked_img else R.drawable.unchecked_img),
                colorFilter = ColorFilter.tint(hexToColor(item.color)),
                contentDescription = "Checkbox",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        val updatedTask = item.copy(isDone = !item.isDone)
                        onChecked.invoke(updatedTask)
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
                    .clickable { onEdit.invoke(item) }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete",
                modifier = Modifier
                    .size(20.dp)
                    .clickable { showDialog = true }
            )

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                    },
                    title = {
                        Text(text = stringResource(R.string.question_delete))
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onDelete.invoke(item)
                                showDialog = false
                            }
                        ) {
                            Text(stringResource(R.string.confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                            }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                    },
                    properties = DialogProperties(dismissOnClickOutside = false)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}