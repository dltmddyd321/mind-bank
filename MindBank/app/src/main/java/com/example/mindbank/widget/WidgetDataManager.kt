package com.example.mindbank.widget

import android.content.Context
import androidx.room.Room
import com.example.mindbank.db.TodoDao
import com.example.mindbank.db.TodoDatabase
import com.example.mindbank.db.data.Task

object WidgetDataManager {

    private var todoDao: TodoDao? = null

    fun fetchTodoList(context: Context): List<Task> {
        val database = Room.databaseBuilder(
            context.applicationContext,
            TodoDatabase::class.java,
            "todo.db"
        ).build()
        todoDao = database.todoDao()
        return todoDao?.getAllSaveData() ?: emptyList()
    }

    fun insertOrUpdate(task: Task) {
        todoDao?.insertOrUpdate(task)
    }
}