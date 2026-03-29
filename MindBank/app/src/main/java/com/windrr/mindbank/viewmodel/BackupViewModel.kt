package com.windrr.mindbank.viewmodel

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.windrr.mindbank.db.MemoRepository
import com.windrr.mindbank.db.TodoRepository
import com.windrr.mindbank.db.data.Memo
import com.windrr.mindbank.db.data.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class BackupData(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val memos: List<Memo> = emptyList(),
    val todos: List<Task> = emptyList()
)

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val memoRepository: MemoRepository,
    private val todoRepository: TodoRepository
) : ViewModel() {

    suspend fun export(context: Context): Uri = withContext(Dispatchers.IO) {
        val memos = memoRepository.getAllData()
        val todos = todoRepository.getAllTodos()
        val backup = BackupData(memos = memos, todos = todos)
        val json = Gson().toJson(backup)

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(context.cacheDir, "mindbank_backup_$timestamp.json")
        file.writeText(json)

        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    /**
     * @return Pair(메모 복원 수, 할일 복원 수)
     */
    suspend fun import(context: Context, uri: Uri): Pair<Int, Int> = withContext(Dispatchers.IO) {
        val json = context.contentResolver.openInputStream(uri)
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: error("파일을 읽을 수 없습니다")

        val backup = Gson().fromJson(json, BackupData::class.java)
            ?: error("올바른 백업 파일이 아닙니다")

        backup.memos.forEach { memoRepository.insert(it.copy(id = 0)) }
        backup.todos.forEach { todoRepository.saveTodo(it.copy(id = 0)) }

        Pair(backup.memos.size, backup.todos.size)
    }
}
