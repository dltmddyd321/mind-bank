package com.example.mindbank.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mindbank.data.SaveData

@Dao
interface SaveDataDao {

    @Query("SELECT * FROM save_model")
    fun getAllSaveData(): List<SaveData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(saveData: SaveData)

    @Query("DELETE FROM save_model WHERE id = :id")
    fun deleteById(id: String): Int

    @Query("DELETE FROM save_model")
    fun deleteAll(): Int
}