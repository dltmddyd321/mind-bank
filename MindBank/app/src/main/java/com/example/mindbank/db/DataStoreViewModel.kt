package com.example.mindbank.db

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * App Open Password와 Save되지 않은 작성 중 데이터를 저장
 */
@HiltViewModel
class DataStoreViewModel @Inject constructor(
    private val datastoreRepo: DatastoreRepo
) : ViewModel() {
    suspend fun getUnSavedData(): String = withContext(Dispatchers.IO) {
        datastoreRepo.getString("UNSAVED") ?: ""
    }

    fun setUnSavedData(data: String) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString("UNSAVED", data)
        }
    }

    suspend fun getPassWord() {
        datastoreRepo.getString("PASSWORD") ?: ""
    }

    fun setPassword(data: String) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString("PASSWORD", data)
        }
    }
}