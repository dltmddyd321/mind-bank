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
 * App Open Password 데이터를 저장
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
        const val KEY_LAST_COLOR = "LAST_COLOR"
    }

    suspend fun getLastColor(): String = datastoreRepo.getString(KEY_LAST_COLOR) ?: ""

    fun setLastColor(color: String) {
        CoroutineScope(Dispatchers.IO).launch {
            datastoreRepo.putString(KEY_LAST_COLOR, color)
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