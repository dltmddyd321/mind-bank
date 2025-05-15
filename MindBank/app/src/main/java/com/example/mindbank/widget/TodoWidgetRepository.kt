package com.example.mindbank.widget

import android.content.Context
import com.example.mindbank.db.TodoDao
import com.example.mindbank.db.data.Task
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class TodoWidgetRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val todoDao: TodoDao,
) {
    fun getTodoList(): List<Task> = runBlocking(Dispatchers.IO) {
        todoDao.getAllSaveData()
    }

    fun getTodo(id: Int) : Task? = runBlocking(Dispatchers.IO) {
        todoDao.searchById(id)
    }

    fun updateTodo(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            todoDao.updateTodo(
                task.id,
                task.title,
                task.dtUpdated,
                task.color,
                task.isDone,
                task.position
            )
            updateWidget(context)
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface GlanceWidgetRepositoryEntryPoint {
        fun widgetRepository(): TodoWidgetRepository
    }

    companion object {
        fun get(applicationContext: Context): TodoWidgetRepository {
            val widgetModelRepositoryEntryPoint: GlanceWidgetRepositoryEntryPoint = EntryPoints.get(
                applicationContext, GlanceWidgetRepositoryEntryPoint::class.java
            )
            return widgetModelRepositoryEntryPoint.widgetRepository()
        }
    }
}