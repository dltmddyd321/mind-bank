package com.example.mindbank.data

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "save_model")
data class SaveData(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val detail: String,
    val dtCreated: Long,
    val dtUpdated: Long,
    val color: Int? = null
)

enum class Type {
    Memo, Link, Account
}