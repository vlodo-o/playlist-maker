package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.medialib.data.converters.TrackDbConverter
import com.practicum.playlistmaker.medialib.data.impl.MedialibRepositoryImpl
import com.practicum.playlistmaker.medialib.domain.MedialibRepository
import com.practicum.playlistmaker.player.data.TrackPlayerImpl
import com.practicum.playlistmaker.player.domain.api.TrackPlayer
import com.practicum.playlistmaker.search.domain.SearchRepository
import com.practicum.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    single<MedialibRepository> {
        MedialibRepositoryImpl(get(), get())
    }

    single<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<TrackPlayer> {
        TrackPlayerImpl()
    }

    factory { TrackDbConverter() }

}