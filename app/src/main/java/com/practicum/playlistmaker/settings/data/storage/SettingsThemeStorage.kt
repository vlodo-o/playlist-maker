package com.practicum.playlistmaker.settings.data.storage

import android.content.SharedPreferences
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsThemeStorage {
    fun saveThemeSettings(settings: ThemeSettings)
    fun getThemeSettings(): ThemeSettings
}