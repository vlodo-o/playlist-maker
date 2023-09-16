package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.medialib.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.medialib.data.converters.TrackDbConverter
import com.practicum.playlistmaker.medialib.data.impl.FavoriteTrackRepositoryImpl
import com.practicum.playlistmaker.medialib.data.impl.PlaylistRepositoryImpl
import com.practicum.playlistmaker.medialib.domain.FavoriteTrackRepository
import com.practicum.playlistmaker.medialib.domain.PlaylistRepository
import com.practicum.playlistmaker.player.data.TrackPlayerImpl
import com.practicum.playlistmaker.player.domain.api.TrackPlayer
import com.practicum.playlistmaker.search.domain.SearchRepository
import com.practicum.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get())
    }

    single<FavoriteTrackRepository> {
        FavoriteTrackRepositoryImpl(get(), get())
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

    factory { PlaylistDbConverter() }

}