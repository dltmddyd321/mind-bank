package com.example.mindbank.util.alarm.test

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.mindbank.db.data.Task
import com.example.mindbank.util.alarm.AlarmReceiver
import javax.inject.Inject

interface AlarmScheduler {
    fun schedule(context: Context, task: Task)
}

class AndroidAlarmScheduler @Inject constructor() : AlarmScheduler {
    @SuppressLint("ServiceCast", "ScheduleExactAlarm")
    override fun schedule(context: Context, task: Task) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("TODO_TITLE", task.title)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.alarmTime, pendingIntent)
    }
}

class FakeAlarmScheduler : AlarmScheduler {
    val scheduledTasks = mutableListOf<Task>()
    override fun schedule(context: Context, task: Task) {
        scheduledTasks.add(task)
    }
}