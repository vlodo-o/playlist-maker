package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.ui.PlayTimer

class PlayerViewModel (private val interactor: PlayerInteractor): ViewModel() {

    private val playTimer: PlayTimer = PlayTimer(interactor) { _playProgress.postValue(it) }
    private val _playState = MutableLiveData<Boolean>()
    val playState: LiveData<Boolean> = _playState

    private val _playProgress = MutableLiveData<String>()
    val playProgress: LiveData<String> = _playProgress

    init {
        interactor.preparePlayer()
        interactor.setTrackCompletionListener {
            _playState.postValue(false)
            playTimer.pauseTimer()
            _playProgress.postValue("00:00")
        }
    }

    private fun startPlayer() {
        interactor.startPlayer()
        _playState.postValue(true)
        playTimer.startTimer()
    }

    fun pausePlayer() {
        interactor.pausePlayer()
        _playState.postValue(false)
        playTimer.pauseTimer()
    }

    fun playbackControl() {
        when(interactor.getPlayerState()) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
            }
            PlayerState.DEFAULT -> {
                interactor.preparePlayer()
                startPlayer()
            }
        }
    }

    companion object {

        fun getViewModelFactory(trackUrl: String): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PlayerViewModel(
                        Creator.providePlayerInteractor(trackUrl)
                    )
                }
            }
    }

}