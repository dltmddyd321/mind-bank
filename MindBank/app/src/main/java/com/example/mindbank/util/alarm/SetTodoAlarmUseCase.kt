package com.example.mindbank.util.alarm

import com.example.mindbank.db.data.Task
import javax.inject.Inject

class SetTodoAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository
) {
    operator fun invoke(todo: Task) {
        alarmRepository.setAlarm(todo)
    }
}