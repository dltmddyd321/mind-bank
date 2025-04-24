package com.example.mindbank.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mindbank.db.data.Memo

@Dao
interface SaveDataDao {

    @Query("SELECT * FROM save_model")
    fun getAllSaveData(): List<Memo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(memo: Memo)

    @Query("DELETE FROM save_model WHERE id = :id")
    fun deleteById(id: Int): Int

    @Query("DELETE FROM save_model")
    fun deleteAll(): Int

    @Query("SELECT * FROM save_model WHERE title LIKE '%' || :keyword || '%' OR detail LIKE '%' || :keyword || '%'")
    fun searchByKeyword(keyword: String): List<Memo>

    @Query("SELECT * FROM save_model WHERE id = :id LIMIT 1")
    fun getSaveDataById(id: Int): Memo?

    @Query(
        """
    UPDATE save_model 
    SET title = :title, 
        detail = :detail, 
        dtUpdated = :dtUpdated, 
        color = :color 
    WHERE id = :id
"""
    )
    fun updateMemoById(
        id: Int,
        title: String,
        detail: String,
        dtUpdated: Long,
        color: String
    )
}