package com.example.mindbank.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.mindbank.R
import com.example.mindbank.db.data.Task
import com.example.mindbank.util.hexToColor

class TodoListWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val todoRepository = TodoWidgetRepository.get(context)
        val todoList = todoRepository.getTodoList()
        provideContent {
            TodoListWidgetLayout(todoList)
        }
    }

    @Composable
    fun TodoListWidgetLayout(todoList: List<Task>) {
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(8.dp).background(Color.White)
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Horizontal.Start,
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Text(
                    text = "할일 >",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )

                Image(
                    provider = ImageProvider(R.drawable.baseline_settings_24),
                    contentDescription = "설정",
                    modifier = GlanceModifier.size(24.dp).clickable {
                        // 여기에 설정 액션 넣기 (예: 설정 Activity 이동)
                    }
                )
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            androidx.glance.appwidget.lazy.LazyColumn {
                items(todoList) { todo ->
                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = GlanceModifier.size(24.dp).clickable(
                                onClick = actionRunCallback<ToggleTodoAction>(
                                    actionParametersOf(ActionParameters.Key<Int>("task_id") to todo.id)
                                )
                            ),
                            provider = ImageProvider(if (todo.isDone) R.drawable.checked_img else R.drawable.unchecked_img),
                            contentDescription = "CheckBox",
                            colorFilter = ColorFilter.tint(ColorProvider(hexToColor(todo.color)))
                        )

                        Spacer(modifier = GlanceModifier.width(8.dp))

                        Text(todo.title, style = TextStyle(color = ColorProvider(Color.Black)))

                        Spacer(modifier = GlanceModifier.width(4.dp))
                    }
                }
            }
        }
    }
}

class ToggleTodoAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val taskId = parameters[ActionParameters.Key<Int>("task_id")] ?: return

        val repo = TodoWidgetRepository.get(context)
        val task = repo.getTodo(taskId) ?: return

        val updated = task.copy(isDone = !task.isDone)
        repo.updateTodo(updated)
    }
}

suspend fun updateWidget(context: Context) {
    val manager = GlanceAppWidgetManager(context)
    val glanceIds = manager.getGlanceIds(TodoListWidget::class.java)
    glanceIds.forEach { glanceId ->
        TodoListWidget().update(context, glanceId)
    }
}