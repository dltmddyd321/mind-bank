package com.example.mindbank.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_task")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val dtCreated: Long,
    val dtUpdated: Long,
    val color: String,
    var isDone: Boolean,
    var position: Long,
    val alarmTime: Long = -1L
)