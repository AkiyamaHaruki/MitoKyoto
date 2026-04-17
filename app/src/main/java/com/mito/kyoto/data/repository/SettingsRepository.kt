package com.mito.kyoto.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mito.kyoto.ui.theme.LocaleManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val languageKey = stringPreferencesKey(LocaleManager.KEY_APP_LANGUAGE)

    suspend fun saveLanguage(languageCode: String) {
        context.dataStore.edit { prefs ->
            prefs[languageKey] = languageCode
        }
        applyLanguage(languageCode)
    }

    private fun applyLanguage(languageCode: String) {
        val localeList = when (languageCode) {
            LocaleManager.LANG_JA -> LocaleListCompat.forLanguageTags("ja")
            LocaleManager.LANG_ZH_TW -> LocaleListCompat.forLanguageTags("zh-TW")
            LocaleManager.LANG_EN -> LocaleListCompat.forLanguageTags("en")
            else -> LocaleListCompat.forLanguageTags("ja")
        }
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    suspend fun initLanguage() {
        val savedLanguage = context.dataStore.data.map { prefs ->
            prefs[languageKey] ?: LocaleManager.LANG_JA
        }.let { flow ->
            var value = LocaleManager.LANG_JA
            flow.collect { value = it }
            value
        }
        applyLanguage(savedLanguage)
    }

    fun getLanguageFlow(): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[languageKey] ?: LocaleManager.LANG_JA
        }
    }
}