package com.practicum.playlistmaker.medialib.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialib.domain.PlaylistInteractor
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistContentViewModel (
private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private lateinit var tracks: List<Track>

    private val _playlistTracks = MutableLiveData<List<Track>>()
    val playlistTracks: LiveData<List<Track>> = _playlistTracks

    fun sumTracksTime(playlistModel: PlaylistModel): String {
        return ""
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

    fun deleteTrackFromPlaylist(playlistId: Int, trackId: String) {

    }

}