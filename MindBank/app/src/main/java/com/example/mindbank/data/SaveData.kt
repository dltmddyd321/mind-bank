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
    val img: Bitmap? = null,
    val color: Int? = null //TODO: 컬러값 저장하는 방식 알아보기
)

enum class Type {
    Memo, Link, Account
}