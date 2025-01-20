package com.example.mindbank.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mindbank.data.Task
import com.example.mindbank.db.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository
): ViewModel() {
    suspend fun getAlData(): List<Task>
}