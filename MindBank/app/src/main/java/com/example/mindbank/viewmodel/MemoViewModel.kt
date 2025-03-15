package com.example.mindbank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindbank.data.Memo
import com.example.mindbank.db.MemoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MemoViewModel @Inject constructor(
    private val memoRepository: MemoRepository
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

    private suspend fun getAllData(): List<Memo> = withContext(viewModelScope.coroutineContext) {
        memoRepository.getAllData().sortedWith(memoComparator)
    }

    suspend fun searchByKeyword(keyword: String): List<Memo> = withContext(viewModelScope.coroutineContext) {
        memoRepository.searchByKeyword(keyword).sortedWith(memoComparator)
    }

    suspend fun searchById(id: Int): Memo? = withContext(viewModelScope.coroutineContext) {
        memoRepository.searchById(id)
    }

    fun insertData(data: Memo) {
        viewModelScope.launch(Dispatchers.IO) {
            memoRepository.insertOrUpdate(data)
            loadMemoList()
        }
    }

    fun deleteData(data: Memo) {
        viewModelScope.launch(Dispatchers.IO) {
            memoRepository.delete(data)
            loadMemoList()
        }
    }

    fun clear() {
        memoRepository.clear()
    }
}