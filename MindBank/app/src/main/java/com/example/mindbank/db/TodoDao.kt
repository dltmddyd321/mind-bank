package com.example.mindbank.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mindbank.data.SaveData
import com.example.mindbank.data.Task

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo_task")
    fun getAllSaveData(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(saveData: Task)

    @Query("DELETE FROM todo_task WHERE id = :id")
    fun deleteById(id: Int): Int

    @Query("DELETE FROM todo_task")
    fun deleteAll(): Int

    @Query("SELECT * FROM todo_task WHERE title LIKE '%' || :keyword")
    fun searchByKeyword(keyword: String): List<Task>

    @Query("SELECT * FROM todo_task WHERE id = :id LIMIT 1")
    fun searchById(id: Int): Task?
}