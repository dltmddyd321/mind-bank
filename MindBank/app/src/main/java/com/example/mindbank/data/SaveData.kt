package com.example.mindbank.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "save_model")
data class SaveData(
    @PrimaryKey val id: String? = null,
    val title: String,
    val detail: String,
    val dtCreated: Long,
    val dtUpdated: Long,
    val color: String
)

