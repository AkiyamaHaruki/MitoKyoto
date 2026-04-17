package com.mito.kyoto.ui.theme

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import java.util.*

object LocaleManager {
    const val KEY_APP_LANGUAGE = "app_language"

    // 支持的语言
    const val LANG_JA = "ja"     // 日本語
    const val LANG_ZH_TW = "zh-TW" // 台灣中國語
    const val LANG_EN = "en"     // English

    fun applyLanguage(context: Context, languageCode: String) {
        val locale = when (languageCode) {
            LANG_JA -> Locale("ja")
            LANG_ZH_TW -> Locale("zh", "TW")
            LANG_EN -> Locale("en")
            else -> Locale.getDefault()
        }
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun getAppLocale(context: Context): Locale {
        val config = context.resources.configuration
        return config.locales.get(0) ?: Locale.getDefault()
    }

    fun getCurrentLanguageCode(context: Context): String {
        val locale = getAppLocale(context)
        return when {
            locale.language == "ja" -> LANG_JA
            locale.language == "zh" && locale.country == "TW" -> LANG_ZH_TW
            locale.language == "en" -> LANG_EN
            else -> LANG_JA // 默认日语
        }
    }
}

@Composable
fun getLanguageDisplayName(languageCode: String): String {
    return when (languageCode) {
        LocaleManager.LANG_JA -> "日本語"
        LocaleManager.LANG_ZH_TW -> "台灣中國語"
        LocaleManager.LANG_EN -> "English"
        else -> "日本語"
    }
}