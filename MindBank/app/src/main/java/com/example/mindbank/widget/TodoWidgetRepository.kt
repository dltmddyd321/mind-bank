package com.example.mindbank.widget

import android.content.Context
import com.example.mindbank.db.TodoDao
import com.example.mindbank.db.data.Task
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TodoWidgetRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val todoDao: TodoDao,
) {
    private val taskComparator = compareByDescending<Task> { it.position }

    fun getTodoList(): List<Task> = runBlocking(Dispatchers.IO) {
        todoDao.getAllSaveData().sortedWith(taskComparator)
    }

    fun getTodo(id: Int): Task? = runBlocking(Dispatchers.IO) {
        todoDao.searchById(id)
    }

    suspend fun updateTodo(task: Task) {
        todoDao.updateTodo(
            task.id,
            task.title,
            task.dtUpdated,
            task.color,
            task.isDone,
            task.position
        )
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