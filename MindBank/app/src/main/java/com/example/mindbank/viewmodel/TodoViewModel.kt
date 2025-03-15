package com.example.mindbank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindbank.data.Task
import com.example.mindbank.db.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository
): ViewModel() {

    private val _todos = MutableStateFlow<List<Task>>(emptyList())
    val todos: StateFlow<List<Task>> = _todos.asStateFlow()

    init {
        loadTodoList()
    }

    fun loadTodoList() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = getAllData()
            _todos.value = data
        }
    }

    private val taskComparator = compareByDescending<Task> { it.position }

    private suspend fun getAllData(): List<Task> = withContext(viewModelScope.coroutineContext) {
        todoRepository.getAllTodos().sortedWith(taskComparator)
    }

    fun updateTodo(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.updateTodo(task)
            loadTodoList()
        }
    }

    suspend fun searchByKeyword(keyword: String): List<Task> = withContext(viewModelScope.coroutineContext) {
        todoRepository.searchByKeyword(keyword).sortedWith(taskComparator)
    }

    suspend fun searchById(id: Int): Task? = withContext(viewModelScope.coroutineContext) {
        todoRepository.searchById(id)
    }

    fun deleteTodo(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.delete(id)
            loadTodoList()
        }
    }

    fun clear() {
        todoRepository.clear()
    }
}