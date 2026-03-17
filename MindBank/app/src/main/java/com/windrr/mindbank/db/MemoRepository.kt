package com.windrr.mindbank.db

import com.windrr.mindbank.db.data.Memo
import kotlinx.coroutines.Dispatchers
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

    suspend fun insert(data: Memo) = withContext(Dispatchers.IO) {
        dataDao.insertOrUpdate(data)
    }

    suspend fun update(data: Memo) = withContext(Dispatchers.IO) {
        dataDao.updateMemoById(
            data.id,
            data.title,
            data.detail,
            data.dtUpdated,
            data.color
        )
    }

    suspend fun delete(data: Memo) = withContext(Dispatchers.IO) {
        dataDao.deleteById(data.id)
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        dataDao.deleteAll()
    }
}