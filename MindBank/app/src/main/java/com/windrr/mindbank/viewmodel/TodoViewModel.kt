package com.windrr.mindbank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windrr.mindbank.db.data.Task
import com.windrr.mindbank.db.TodoRepository
import com.windrr.mindbank.util.alarm.SetTodoAlarmUseCase
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
    private val todoRepository: TodoRepository,
    private val setTodoAlarmUseCase: SetTodoAlarmUseCase
): ViewModel() {

    private val _todos = MutableStateFlow<List<Task>>(emptyList())
    val todos: StateFlow<List<Task>> = _todos.asStateFlow()

    init {
        loadTodoList()
    }

    fun onSetAlarm(todo: Task) {
        setTodoAlarmUseCase(todo)
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

    fun saveTodo(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.saveTodo(task)
            loadTodoList()
        }
    }

    fun updateTodo(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.update(task)
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