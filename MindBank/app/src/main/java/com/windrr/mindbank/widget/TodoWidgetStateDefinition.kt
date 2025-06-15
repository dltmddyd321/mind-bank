package com.windrr.mindbank.widget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.state.GlanceStateDefinition
import java.io.File

object TodoWidgetStateDefinition: GlanceStateDefinition<Preferences> {
    private const val NAME = "todo_widget_updated_prefs"

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<Preferences> {
        return context.dataStore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return File(context.applicationContext.filesDir, "datastore/$NAME")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = NAME)

}