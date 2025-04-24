package com.example.mindbank.db

import com.example.mindbank.db.data.Memo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MemoRepository @Inject constructor(
    private val dataDao: SaveDataDao
) {
    suspend fun getAllData(): List<Memo> = withContext(Dispatchers.IO) {
        dataDao.getAllSaveData()
    }

    suspend fun searchByKeyword(keyword: String): List<Memo> = withContext(Dispatchers.IO) {
        dataDao.searchByKeyword(keyword)
    }

    suspend fun searchById(id: Int): Memo? = withContext(Dispatchers.IO) {
        dataDao.getSaveDataById(id)
    }

    fun insert(data: Memo) {
        CoroutineScope(Dispatchers.IO).launch {
            dataDao.insertOrUpdate(data)
        }
    }

    fun update(data: Memo) {
        CoroutineScope(Dispatchers.IO).launch {
            dataDao.updateMemoById(
                data.id,
                data.title,
                data.detail,
                data.dtUpdated,
                data.color
            )
        }
    }

    fun delete(data: Memo) {
        CoroutineScope(Dispatchers.IO).launch {
            dataDao.deleteById(data.id)
        }
    }

    fun clear() {
        CoroutineScope(Dispatchers.IO).launch {
            dataDao.deleteAll()
        }
    }
}