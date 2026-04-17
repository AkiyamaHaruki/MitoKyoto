package com.mito.kyoto.data.repository

import android.content.Context
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
        LocaleManager.applyLanguage(context, languageCode)
    }

    fun getLanguageFlow(): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[languageKey] ?: LocaleManager.LANG_JA // 默认日语
        }
    }

    suspend fun getCurrentLanguage(): String {
        return context.dataStore.data.map { prefs ->
            prefs[languageKey] ?: LocaleManager.LANG_JA
        }.let { flow ->
            var value = LocaleManager.LANG_JA
            flow.collect { value = it }
            value
        }
    }
}