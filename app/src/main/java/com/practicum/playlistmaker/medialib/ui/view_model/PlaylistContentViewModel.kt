package com.practicum.playlistmaker.medialib.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialib.domain.PlaylistInteractor
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistContentViewModel (
private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private lateinit var tracks: List<Track>

    private val _playlistTracks = MutableLiveData<List<Track>>()
    val playlistTracks: LiveData<List<Track>> = _playlistTracks

    private val _tracksCount = MutableLiveData<Int>()
    val tracksCount: LiveData<Int> = _tracksCount

    private val _playlistDuration = MutableLiveData<String>()
    val playlistDuration: LiveData<String> = _playlistDuration

    fun sumTracksTime(playlistModel: PlaylistModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val duration = playlistInteractor.getPlaylistDuration(playlistModel)
            _playlistDuration.postValue(duration)
        }
    }

    fun getAllPlaylistTracks(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor
                .getAllPlaylistTracks(playlistId)
                .collect { trackList ->
                    tracks = trackList
                    _playlistTracks.postValue(trackList)
                }
        }
    }

    fun deleteTrackFromPlaylist(playlist: PlaylistModel, trackId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.deleteTrackFromPlaylist(playlist, trackId)
            withContext(Dispatchers.Main) {
                getAllPlaylistTracks(playlist.id)
                _tracksCount.postValue(playlist.tracksCount.dec())
                sumTracksTime(playlist)
            }
        }
    }

}