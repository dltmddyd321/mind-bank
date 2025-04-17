package com.example.mindbank.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mindbank.data.Memo
import com.example.mindbank.data.Task

@Database(entities = [Memo::class], version = 2)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun saveDataDao(): SaveDataDao
}

@Database(entities = [Task::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}