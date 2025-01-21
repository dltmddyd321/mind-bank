package com.example.mindbank.db

import com.example.mindbank.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    suspend fun getAllTodos(): List<Task> = withContext(Dispatchers.IO) {
        todoDao.getAllSaveData()
    }
}