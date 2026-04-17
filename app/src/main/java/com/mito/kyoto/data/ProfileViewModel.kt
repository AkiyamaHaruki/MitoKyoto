package com.mito.kyoto.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mito.kyoto.data.repository.SettingsRepository
import com.mito.kyoto.ui.theme.LocaleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)

    private val _currentLanguage = MutableStateFlow(LocaleManager.LANG_JA)
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getLanguageFlow().collect { languageCode ->
                _currentLanguage.value = languageCode
            }
        }
    }

    fun updateLanguage(languageCode: String) {
        viewModelScope.launch {
            settingsRepository.saveLanguage(languageCode)
        }
    }

    val userName = "秋山 陽"
    val userPosition = "代表取締役社長"
    val userAvatarUrl = ""
}