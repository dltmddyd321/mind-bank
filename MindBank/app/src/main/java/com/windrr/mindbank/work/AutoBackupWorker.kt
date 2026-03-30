package com.windrr.mindbank.work

import android.content.Context
import android.util.Log
import androidx.core.content.FileProvider
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.windrr.mindbank.db.MemoRepository
import com.windrr.mindbank.db.TodoRepository
import com.windrr.mindbank.db.data.Memo
import com.windrr.mindbank.db.data.Task
import com.windrr.mindbank.viewmodel.BackupData
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@HiltWorker
class AutoBackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val memoRepository: MemoRepository,
    private val todoRepository: TodoRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            performAutoBackup()
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Auto backup failed", e)
            Result.failure()
        }
    }

    private suspend fun performAutoBackup() {
        val context = applicationContext
        
        // 데이터 가져오기
        val memos = memoRepository.getAllData()
        val todos = todoRepository.getAllTodos()
        
        // 백업 데이터 생성
        val backup = BackupData(memos = memos, todos = todos)
        val json = Gson().toJson(backup)
        
        // 백업 파일 생성
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "mindbank_auto_backup_$timestamp.json"
        val backupFile = File(context.cacheDir, fileName)
        backupFile.writeText(json)
        
        // 다운로드 폴더에 복사 (사용자가 쉽게 찾을 수 있도록)
        val downloadsDir = File(context.getExternalFilesDir(null), "Backups")
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        
        val finalBackupFile = File(downloadsDir, fileName)
        backupFile.copyTo(finalBackupFile, overwrite = true)
        
        // 오래된 백업 파일 정리 (최근 7개만 유지)
        cleanupOldBackups(downloadsDir)
        
        Log.i(TAG, "Auto backup completed: ${finalBackupFile.absolutePath}")
    }
    
    private fun cleanupOldBackups(backupDir: File) {
        try {
            val backupFiles = backupDir.listFiles { file ->
                file.name.startsWith("mindbank_auto_backup_") && file.name.endsWith(".json")
            }?.sortedByDescending { it.lastModified() }
            
            backupFiles?.let { files ->
                if (files.size > MAX_BACKUP_FILES) {
                    files.drop(MAX_BACKUP_FILES).forEach { file ->
                        file.delete()
                        Log.d(TAG, "Deleted old backup: ${file.name}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cleanup old backups", e)
        }
    }
    
    companion object {
        private const val TAG = "AutoBackupWorker"
        private const val MAX_BACKUP_FILES = 7
        const val WORK_NAME = "auto_backup_work"
    }
}
