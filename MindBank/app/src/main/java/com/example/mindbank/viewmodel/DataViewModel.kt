package com.example.mindbank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindbank.data.Memo
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

    private val _memos = MutableStateFlow<List<Memo>>(emptyList())
    val memos: StateFlow<List<Memo>> = _memos.asStateFlow()

    init {
        loadMemoList()
    }

    fun loadMemoList() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = getAllData()
            _memos.value = data
        }
    }

    private val memoComparator = compareByDescending<Memo> { it.dtCreated }

    suspend fun getAllData(): List<Memo> = withContext(viewModelScope.coroutineContext) {
        dataRepository.getAllData().sortedWith(memoComparator)
    }

    suspend fun searchByKeyword(keyword: String): List<Memo> = withContext(viewModelScope.coroutineContext) {
        dataRepository.searchByKeyword(keyword).sortedWith(memoComparator)
    }

    suspend fun searchById(id: Int): Memo? = withContext(viewModelScope.coroutineContext) {
        dataRepository.searchById(id)
    }

    fun insertData(data: Memo) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.insertOrUpdate(data)
            loadMemoList()
        }
    }

    fun deleteData(data: Memo) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.delete(data)
            loadMemoList()
        }
    }

    fun clear() {
        dataRepository.clear()
    }
}