package com.windrr.mindbank.viewmodel

import androidx.lifecycle.ViewModel
import com.windrr.mindbank.db.DatastoreRepo
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
    suspend fun getUnSavedData(): UnSaved = withContext(Dispatchers.IO) {
        UnSaved(
            datastoreRepo.getString("UNSAVED_TITLE") ?: "",
            datastoreRepo.getString("UNSAVED_LINK") ?: "",
            datastoreRepo.getString("UNSAVED_MEMO") ?: "",
            datastoreRepo.getString("UNSAVED_COLOR") ?: ""
        )
    }

    fun setUnSavedTitle(data: String) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString("UNSAVED_TITLE", data)
        }
    }

    fun setUnSavedLink(data: String) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString("UNSAVED_LINK", data)
        }
    }

    fun setUnSavedMemo(data: String) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString("UNSAVED_MEMO", data)
        }
    }

    fun setUnSavedColor(data: String) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString("UNSAVED_COLOR", data)
        }
    }

    fun clearUnSaved() {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString("UNSAVED_TITLE", "")
            datastoreRepo.putString("UNSAVED_MEMO", "")
            datastoreRepo.putString("UNSAVED_COLOR", "")
        }
    }

    suspend fun getPassWord(): String = datastoreRepo.getString("PASSWORD") ?: ""

    fun setPassword(data: String) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString("PASSWORD", data)
        }
    }
}

data class UnSaved(
    val title: String,
    val link: String,
    val memo: String,
    val color: String
)