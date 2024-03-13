package com.example.mindbank.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mindbank.data.SaveData

@Database(entities = [SaveData::class], version = 1)
abstract class SaveModelDatabase : RoomDatabase() {
    abstract fun locationDao():
}