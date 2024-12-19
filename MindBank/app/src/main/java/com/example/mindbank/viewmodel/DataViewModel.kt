package com.example.mindbank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindbank.data.SaveData
import com.example.mindbank.db.DataRepository
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

    suspend fun searchByKeyword(keyword: String): List<SaveData> = withContext(viewModelScope.coroutineContext) {
        dataRepository.searchByKeyword(keyword)
    }

    suspend fun searchByKeyword(id: Int): SaveData? = withContext(viewModelScope.coroutineContext) {
        dataRepository.searchById(id)
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