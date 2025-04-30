package com.example.mindbank.util.alarm

import com.example.mindbank.db.data.Task

interface AlarmRepository {
    fun setAlarm(todo: Task)
    fun cancelAlarm(todo: Task)
}