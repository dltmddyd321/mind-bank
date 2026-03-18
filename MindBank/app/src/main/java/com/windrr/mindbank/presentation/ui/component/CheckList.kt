package com.windrr.mindbank.presentation.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.windrr.mindbank.R
import com.windrr.mindbank.db.data.Task
import com.windrr.mindbank.presentation.ui.theme.SpaceBorder
import com.windrr.mindbank.presentation.ui.theme.SpaceCloud
import com.windrr.mindbank.presentation.ui.theme.SpaceSurface
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
    var showDialog by remember { mutableStateOf(false) }
    
    val todoColor = hexToColor(item.color)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = SpaceBorder.copy(alpha = 0.3f),
                spotColor = SpaceBorder.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SpaceSurface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (item.isDone) 
                todoColor.copy(alpha = 0.3f)
            else 
                SpaceBorder.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Custom checkbox with space theme styling
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (item.isDone) 
                            todoColor.copy(alpha = 0.8f)
                        else 
                            SpaceBorder.copy(alpha = 0.3f)
                    )
                    .border(
                        width = 2.dp,
                        color = todoColor.copy(alpha = if (item.isDone) 1.0f else 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        val updatedTask = item.copy(isDone = !item.isDone)
                        onChecked.invoke(updatedTask)
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        id = if (item.isDone) R.drawable.checked_img else R.drawable.unchecked_img
                    ),
                    colorFilter = ColorFilter.tint(
                        if (item.isDone) 
                            Color.White
                        else 
                            todoColor.copy(alpha = 0.7f)
                    ),
                    contentDescription = "Checkbox",
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (item.isDone) FontWeight.Normal else FontWeight.Medium,
                    color = if (item.isDone) 
                        SpaceCloud.copy(alpha = 0.5f) 
                    else 
                        SpaceCloud,
                    textDecoration = if (item.isDone) TextDecoration.LineThrough else null
                ),
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Edit button with space theme
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = SpaceCloud.copy(alpha = 0.7f),
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onEdit.invoke(item) }
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Delete button with space theme
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete",
                tint = SpaceCloud.copy(alpha = 0.7f),
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { showDialog = true }
                    .padding(4.dp)
            )
        }
    }

    // Improved delete dialog with space theme
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.question_delete),
                    color = SpaceCloud,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = "이 할 일을 삭제하시겠습니까?",
                    color = SpaceCloud.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete.invoke(item)
                        showDialog = false
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(todoColor.copy(alpha = 0.2f))
                ) {
                    Text(
                        stringResource(R.string.confirm),
                        color = todoColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SpaceBorder.copy(alpha = 0.3f))
                ) {
                    Text(
                        stringResource(R.string.cancel),
                        color = SpaceCloud.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            containerColor = SpaceSurface,
            properties = DialogProperties(dismissOnClickOutside = false)
        )
    }
}