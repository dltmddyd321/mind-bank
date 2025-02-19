package com.example.mindbank.db

import com.example.mindbank.data.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    suspend fun getAllTodos(): List<Task> = withContext(Dispatchers.IO) {
        todoDao.getAllSaveData()
    }

    fun updateTodo(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            todoDao.insertOrUpdate(task)
        }
    }

    suspend fun searchByKeyword(keyword: String): List<Task> = withContext(Dispatchers.IO) {
        todoDao.searchByKeyword(keyword)
    }

    suspend fun searchById(id: Int): Task? = withContext(Dispatchers.IO) {
        todoDao.searchById(id)
    }

    fun delete(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            todoDao.deleteById(id)
        }
    }
}