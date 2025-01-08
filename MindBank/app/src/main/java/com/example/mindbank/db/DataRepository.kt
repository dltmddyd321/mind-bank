package com.example.mindbank.db

import com.example.mindbank.data.SaveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.system.exitProcess

class DataRepository @Inject constructor(
    private val dataDao: SaveDataDao
) {
    suspend fun getAllData(): List<SaveData> = withContext(Dispatchers.IO) {
        dataDao.getAllSaveData()
    }

    suspend fun searchByKeyword(keyword: String): List<SaveData> = withContext(Dispatchers.IO) {
        dataDao.searchByKeyword(keyword)
    }

    suspend fun searchById(id: Int): SaveData? = withContext(Dispatchers.IO) {
        dataDao.getSaveDataById(id)
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

fun main() {
    val n = readLine()!!.toInt()
    val result = StringBuilder()

    fun isGoodSequence(seq: String): Boolean {
        for (len in 1..(seq.length / 2)) {
            if (seq.substring(seq.length - len * 2, seq.length - len) == seq.substring(seq.length - len)) {
                return false
            }
        }
        return true
    }

    fun backtrack(sequence: String) {
        if (sequence.length == n) {
            println(sequence)
            exitProcess(0) // 첫 번째로 찾은 결과 출력 후 종료
        }

        for (i in 1..3) {
            val newSequence = sequence + i
            if (isGoodSequence(newSequence)) {
                backtrack(newSequence)
            }
        }
    }

    backtrack("")
}