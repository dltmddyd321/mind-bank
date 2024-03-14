package com.example.mindbank.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mindbank.data.SaveData
import com.example.mindbank.util.ColorConverter

@Database(entities = [SaveData::class], version = 1)
@TypeConverters(ColorConverter::class)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun saveDataDao(): SaveDataDao
}