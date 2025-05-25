package com.example.mindbank.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.LocaleListCompat
import java.util.Locale

class AppLanguageState {
    var currentLocale: Locale by mutableStateOf(Locale.getDefault())

    fun updateLocale(langCode: String) {
        val newLocale = Locale(langCode)
        currentLocale = newLocale
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(newLocale))
    }
}