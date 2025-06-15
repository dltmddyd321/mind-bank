package com.windrr.mindbank.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.windrr.mindbank.db.data.Memo
import com.windrr.mindbank.db.data.Task

@Database(entities = [Memo::class], version = 2)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun saveDataDao(): SaveDataDao
}

@Database(entities = [Task::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}