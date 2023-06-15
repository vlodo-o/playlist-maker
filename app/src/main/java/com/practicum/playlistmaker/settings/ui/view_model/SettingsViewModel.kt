package com.practicum.playlistmaker.settings.ui.view_model

import android.content.Context
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel (
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
    private val application: App
        ): AndroidViewModel(application) {

    private val _themeSettingsState = MutableLiveData<ThemeSettings>()
    val themeSettingsState: LiveData<ThemeSettings> = _themeSettingsState

    init {
        _themeSettingsState.postValue(settingsInteractor.getThemeSettings())
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        val settings = ThemeSettings(darkThemeEnabled)
        _themeSettingsState.postValue(settings)
        settingsInteractor.updateThemeSetting(settings)
        application.switchTheme(darkThemeEnabled)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun supportEmail() {
        sharingInteractor.openSupport()
    }

    fun openAgreement() {
        sharingInteractor.openTerms()
    }

    companion object {

        fun getViewModelFactory(context: Context): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App
                    SettingsViewModel(
                        Creator.provideSettingsInteractor(context),
                        Creator.provideSharingInteractor(context),
                        application
                    )
                }
            }
    }


}