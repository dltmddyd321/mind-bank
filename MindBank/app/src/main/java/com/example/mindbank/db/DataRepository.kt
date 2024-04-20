package com.example.mindbank.db

import com.example.mindbank.data.SaveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val dataDao: SaveDataDao
) {
    suspend fun getAllData(): List<SaveData> = withContext(Dispatchers.IO) {
        dataDao.getAllSaveData()
    }

    fun insertOrUpdate(data: SaveData) {
        dataDao.insertOrUpdate(data)
    }

    fun delete(data: SaveData) {
        dataDao.deleteById(data.id)
    }

    fun clear() {
        dataDao.deleteAll()
    }

}