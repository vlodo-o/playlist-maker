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
            _playState.value = false
            playTimer.pauseTimer()
            _playProgress.value = TIMER_START
        }
    }

    private fun startPlayer() {
        interactor.startPlayer()
        _playState.value = true
        playTimer.startTimer()
    }

    fun pausePlayer() {
        interactor.pausePlayer()
        _playState.value = false
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

        const val TIMER_START = "00:00"

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