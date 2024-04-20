package com.example.mindbank.db

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindbank.data.SaveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {
    suspend fun getAllData(): List<SaveData> = withContext(viewModelScope.coroutineContext) {
        dataRepository.getAllData()
    }

    fun insertData(data: SaveData) {
        dataRepository.insertOrUpdate(data)
    }

    fun deleteData(data: SaveData) {
        dataRepository.delete(data)
    }

    fun clear() {
        dataRepository.clear()
    }
}