package com.example.mindbank.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "save_model")
data class Memo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var title: String,
    var detail: String,
    val dtCreated: Long,
    var dtUpdated: Long,
    var color: String
)

