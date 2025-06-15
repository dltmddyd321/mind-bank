package com.windrr.mindbank.db

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.windrr.mindbank.util.Constants
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.Arrival_Datastore)

class DatastoreRepoImpl @Inject constructor(
    private val context: Context
) : DatastoreRepo {
    override suspend fun putString(key: String, value: String) {
        val prefsKey = stringPreferencesKey(key)
        context.dataStore.edit {
            it[prefsKey] = value
        }
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        val prefsKey = booleanPreferencesKey(key)
        context.dataStore.edit {
            it[prefsKey] = value
        }
    }

    override suspend fun getString(key: String): String? {
        return try {
            val prefsKey = stringPreferencesKey(key)
            val prefs = context.dataStore.data.first()
            prefs[prefsKey]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun clearPrefs(key: String) {
        val prefsKey = stringPreferencesKey(key)
        context.dataStore.edit {
            if (it.contains(prefsKey)) {
                it.remove(prefsKey)
            }
        }
    }
}