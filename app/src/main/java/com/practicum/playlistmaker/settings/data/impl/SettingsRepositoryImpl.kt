package com.practicum.playlistmaker.settings.data.impl

import com.practicum.playlistmaker.settings.data.SettingsRepository
import com.practicum.playlistmaker.settings.data.storage.SettingsThemeStorage
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(private val storage: SettingsThemeStorage): SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return storage.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        storage.saveThemeSettings(settings)
    }

}