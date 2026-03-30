package com.windrr.mindbank.util

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.firebase.FirebaseApp
import com.windrr.mindbank.work.AutoBackupScheduler
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class CoreApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        AppCompatDelegate.setApplicationLocales(loadSavedLocales())
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val langCode = prefs.getString("language", Locale.getDefault().language) ?: "ko"
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        applicationContext.resources.updateConfiguration(config, resources.displayMetrics)
        initializeAutoBackup()
    }

    private fun loadSavedLocales(): LocaleListCompat = AppCompatDelegate.getApplicationLocales()
    
    private fun initializeAutoBackup() {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val autoBackupEnabled = prefs.getBoolean("auto_backup_enabled", true) // 기본값은 활성화
        AutoBackupScheduler.scheduleAutoBackup(this, autoBackupEnabled)
    }
}