package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialib.domain.FavoriteTrackInteractor
import com.practicum.playlistmaker.medialib.domain.PlaylistInteractor
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import com.practicum.playlistmaker.medialib.ui.models.PlaylistsState
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel (
    private val playerInteractor: PlayerInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor
    ): ViewModel() {

    private val _playState = MutableLiveData<Boolean>()
    val playState: LiveData<Boolean> = _playState

    private val _playProgress = MutableLiveData<String>()
    val playProgress: LiveData<String> = _playProgress

    private val _favoriteState = MutableLiveData<Boolean>()
    val favoriteState: LiveData<Boolean> = _favoriteState

    private val _playlistsState = MutableLiveData<PlaylistsState>()
    val playlistsState: LiveData<PlaylistsState> = _playlistsState

    private val _trackAddedToPlaylist = MutableLiveData<Boolean>()
    val trackAddedToPlaylist: LiveData<Boolean> = _trackAddedToPlaylist

    private var isFavorite = false

    private var timerJob: Job? = null

    private fun startPlayer(trackUrl: String) {
        playerInteractor.startPlayer(trackUrl)
        _playState.value = true
        startTimer()
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        _playState.value = false
        timerJob?.cancel()
    }

    fun stopPlayer() {
        playerInteractor.stopPlayer()
        _playState.value = false
    }

    fun playbackControl(trackUrl: String) {
        when(playerInteractor.getPlayerState()) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer(trackUrl)
            }
            PlayerState.DEFAULT -> {
                startPlayer(trackUrl)
                playerInteractor.setTrackCompletionListener {
                    _playState.value = false
                    timerJob?.cancel()
                    _playProgress.value = TIMER_START
                }
            }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.getPlayerState() == PlayerState.PLAYING) {
                delay(DURATION_UPDATE_DELAY_MS)
                _playProgress.postValue(millisecondsToString(playerInteractor.getCurrentPosition()))
            }
        }
    }

    fun favoriteControl(track: Track) {
        if (isFavorite) {
            isFavorite = false
            _favoriteState.value = false
            viewModelScope.launch {
                favoriteTrackInteractor.deleteTrack(track.trackId)
            }
        }
        else {
            isFavorite = true
            _favoriteState.value = true
            viewModelScope.launch {
                favoriteTrackInteractor.saveTrack(track)
            }
        }
    }

    fun checkFavorite(trackId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                favoriteTrackInteractor.isFavoriteTrack(trackId).collect {
                    isFavorite = it
                    _favoriteState.postValue(isFavorite)
                }
            }
        }
    }

    fun getPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor
                .getPlaylists()
                .collect { playlists ->
                    if (playlists.isNotEmpty()) {
                        _playlistsState.postValue(PlaylistsState.Playlists(playlists))
                    } else {
                        _playlistsState.postValue(PlaylistsState.Empty)
                    }
                }
        }
    }

    fun addToPlaylist(playlist: PlaylistModel, track: Track) {
        viewModelScope.launch {
            if (playlistInteractor.isTrackInPlaylist(playlist, track)) {
                _trackAddedToPlaylist.postValue(false)
            }
            else {
                playlistInteractor.addTrackToPlaylist(playlist, track)
                _trackAddedToPlaylist.postValue(true)
            }
        }
    }

    private fun millisecondsToString(duration: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(duration)
    }

    companion object {
        const val TIMER_START = "00:00"
        private const val DURATION_UPDATE_DELAY_MS = 300L
    }

}