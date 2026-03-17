package com.windrr.mindbank.db

import com.windrr.mindbank.db.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    suspend fun getAllTodos(): List<Task> = withContext(Dispatchers.IO) {
        todoDao.getAllSaveData()
    }

    suspend fun saveTodo(task: Task) = withContext(Dispatchers.IO) {
        todoDao.insertOrUpdate(task)
    }

    suspend fun searchByKeyword(keyword: String): List<Task> = withContext(Dispatchers.IO) {
        todoDao.searchByKeyword(keyword)
    }

    suspend fun searchById(id: Int): Task? = withContext(Dispatchers.IO) {
        todoDao.searchById(id)
    }

    suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        todoDao.deleteById(id)
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        todoDao.deleteAll()
    }

    suspend fun update(task: Task) = withContext(Dispatchers.IO) {
        todoDao.updateTodo(
            task.id,
            task.title,
            task.dtUpdated,
            task.color,
            task.isDone,
            task.position
        )
    }
}