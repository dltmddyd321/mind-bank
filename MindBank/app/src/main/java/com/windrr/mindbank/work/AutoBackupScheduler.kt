package com.windrr.mindbank.work

import android.content.Context
import androidx.work.*
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object AutoBackupScheduler {
    
    fun scheduleAutoBackup(context: Context, enabled: Boolean = true) {
        val workManager = WorkManager.getInstance(context)
        
        if (enabled) {
            // 하루에 한번 실행되는 PeriodicWorkRequest 생성
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED) // 네트워크 불필요
                .setRequiresBatteryNotLow(false) // 배터리 낮아도 실행
                .build()
            
            val periodicWorkRequest = PeriodicWorkRequestBuilder<AutoBackupWorker>(
                1, TimeUnit.DAYS
            )
                .setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.HOURS) // 처음은 1시간 후 실행
                .addTag(AutoBackupWorker.WORK_NAME)
                .build()
            
            // 기존 작업 취소 후 새로운 작업 예약
            workManager.cancelAllWorkByTag(AutoBackupWorker.WORK_NAME)
            workManager.enqueueUniquePeriodicWork(
                AutoBackupWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                periodicWorkRequest
            )
        } else {
            // 자동 백업 비활성화 시 작업 취소
            workManager.cancelAllWorkByTag(AutoBackupWorker.WORK_NAME)
        }
    }
    
    fun isAutoBackupScheduled(context: Context): Boolean {
        val workManager = WorkManager.getInstance(context)
        val workInfos = workManager.getWorkInfosByTag(AutoBackupWorker.WORK_NAME).get()
        return workInfos.isNotEmpty() && workInfos.any { !it.state.isFinished }
    }
}
