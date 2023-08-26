package com.practicum.playlistmaker.settings.data.storage

import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsThemeStorage {
    fun saveThemeSettings(settings: ThemeSettings)
    fun getThemeSettings(): ThemeSettings
}