package com.example.mindbank.db

import com.example.mindbank.data.SaveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val dataDao: SaveDataDao
) {
    suspend fun getAllData(): List<SaveData> = withContext(Dispatchers.IO) {
        dataDao.getAllSaveData()
    }

    suspend fun searchByKeyword(keyword: String): List<SaveData> = withContext(Dispatchers.IO) {
        dataDao.searchByKeyword(keyword)
    }

    fun insertOrUpdate(data: SaveData) {
        CoroutineScope(Dispatchers.IO).launch {
            dataDao.insertOrUpdate(data)
        }
    }

    fun delete(data: SaveData) {
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