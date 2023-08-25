package com.practicum.playlistmaker.di

import android.content.Context
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.storage.SearchHistoryStorage
import com.practicum.playlistmaker.search.data.storage.SharedPrefsHistoryStorage
import com.practicum.playlistmaker.settings.data.storage.SettingsThemeStorage
import com.practicum.playlistmaker.settings.data.storage.SharedPrefsThemeStorage
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single<NetworkClient> {
        RetrofitNetworkClient(androidContext())
    }

    single {
        androidContext()
            .getSharedPreferences(
                "local_storage", Context.MODE_PRIVATE
            )
    }

    single<SearchHistoryStorage> {
        SharedPrefsHistoryStorage(get())
    }

    single<SettingsThemeStorage> {
        SharedPrefsThemeStorage(get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

}