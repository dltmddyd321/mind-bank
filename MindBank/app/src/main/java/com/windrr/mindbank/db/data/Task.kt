package com.windrr.mindbank.db.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity(tableName = "todo_task")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val dtCreated: Long,
    val dtUpdated: Long,
    val color: String,
    var isDone: Boolean,
    var position: Long,
    var alarmTime: Long = -1L
)

data class TodoInWidget(
    val id: Int,
    val title: String,
    val isDone: Boolean,
    val color: String
)

val gson = Gson()

fun String.getTodoData(): TodoInWidget? = runCatching {
    gson.fromJson(this, TodoInWidget::class.java)
}.getOrNull()

fun Task.toStorageString(): String {
    val simpleTask = TodoInWidget(id = id, title = title, isDone = isDone, color = color)
    return Gson().toJson(simpleTask)
}