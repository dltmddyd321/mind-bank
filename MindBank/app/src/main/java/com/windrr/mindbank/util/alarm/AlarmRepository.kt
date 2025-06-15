package com.windrr.mindbank.util.alarm

import com.windrr.mindbank.db.data.Task

interface AlarmRepository {
    fun setAlarm(todo: Task)
    fun cancelAlarm(todo: Task)
}