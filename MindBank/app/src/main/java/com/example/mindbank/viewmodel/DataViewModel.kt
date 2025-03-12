package com.example.mindbank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindbank.data.SaveData
import com.example.mindbank.data.Task
import com.example.mindbank.db.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _memos = MutableStateFlow<List<SaveData>>(emptyList())
    val memos: StateFlow<List<SaveData>> = _memos.asStateFlow()

    init {
        loadMemoList()
    }

    fun loadMemoList() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = getAllData()
            _memos.value = data
        }
    }

    private val memoComparator = compareByDescending<SaveData> { it.dtCreated }

    suspend fun getAllData(): List<SaveData> = withContext(viewModelScope.coroutineContext) {
        dataRepository.getAllData().sortedWith(memoComparator)
    }

    suspend fun searchByKeyword(keyword: String): List<SaveData> = withContext(viewModelScope.coroutineContext) {
        dataRepository.searchByKeyword(keyword).sortedWith(memoComparator)
    }

    suspend fun searchById(id: Int): SaveData? = withContext(viewModelScope.coroutineContext) {
        dataRepository.searchById(id)
    }

    fun insertData(data: SaveData) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.insertOrUpdate(data)
            loadMemoList()
        }
    }

    fun deleteData(data: SaveData) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.delete(data)
            loadMemoList()
        }
    }

    fun clear() {
        dataRepository.clear()
    }
}