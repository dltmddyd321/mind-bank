package com.example.mindbank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindbank.data.Task
import com.example.mindbank.db.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository
): ViewModel() {
    suspend fun getAllData(): List<Task> = withContext(viewModelScope.coroutineContext) {
        todoRepository.getAllTodos()
    }

    fun updateTodo(task: Task) {
        todoRepository.updateTodo(task)
    }

    suspend fun searchByKeyword(keyword: String): List<Task> = withContext(viewModelScope.coroutineContext) {
        todoRepository.searchByKeyword(keyword)
    }

    suspend fun searchById(id: Int): Task? = withContext(viewModelScope.coroutineContext) {
        todoRepository.searchById(id)
    }

    fun deleteTodo(id: Int) {
        todoRepository.delete(id)
    }
}