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

    companion object {
        const val KEY_PASSWORD = "PASSWORD"
        const val KEY_APP_LOCK_ENABLED = "APP_LOCK_ENABLED"
        const val KEY_BIOMETRIC_ENABLED = "BIOMETRIC_ENABLED"
        const val KEY_PASSWORD_SALT = "PASSWORD_SALT"
        const val KEY_PASSWORD_HASH = "PASSWORD_HASH"
    }

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

    suspend fun getPassWord(): String = datastoreRepo.getString(KEY_PASSWORD) ?: ""

    fun setPassword(data: String) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString(KEY_PASSWORD, data)
        }
    }

    suspend fun isAppLockEnabled(): Boolean = datastoreRepo.getString(KEY_APP_LOCK_ENABLED) == "true"

    fun setAppLockEnabled(enabled: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString(KEY_APP_LOCK_ENABLED, enabled.toString())
        }
    }

    suspend fun isBiometricEnabled(): Boolean = datastoreRepo.getString(KEY_BIOMETRIC_ENABLED) == "true"

    fun setBiometricEnabled(enabled: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString(KEY_BIOMETRIC_ENABLED, enabled.toString())
        }
    }

    suspend fun getPasswordSalt(): String = datastoreRepo.getString(KEY_PASSWORD_SALT) ?: ""

    fun setPasswordSalt(value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString(KEY_PASSWORD_SALT, value)
        }
    }

    suspend fun getPasswordHash(): String = datastoreRepo.getString(KEY_PASSWORD_HASH) ?: ""

    fun setPasswordHash(value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString(KEY_PASSWORD_HASH, value)
        }
    }
}

data class UnSaved(
    val title: String,
    val link: String,
    val memo: String,
    val color: String
)