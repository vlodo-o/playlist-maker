package com.practicum.playlistmaker.player.creator

import com.practicum.playlistmaker.player.data.TrackPlayerImpl
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.player.presentation.PlayerView
import com.practicum.playlistmaker.player.presentation.presenter.PlayerPresenter

object Creator {

    fun providePresenter(view: PlayerView, track: Track): PlayerPresenter {
        val interactor = PlayerInteractorImpl(trackPlayer = TrackPlayerImpl(track.previewUrl))
        return PlayerPresenter(
            view = view,
            interactor = interactor,
        )
    }
}