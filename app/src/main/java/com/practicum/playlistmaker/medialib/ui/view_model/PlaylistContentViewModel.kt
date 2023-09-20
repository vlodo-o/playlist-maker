package com.practicum.playlistmaker.medialib.ui.view_model

import android.icu.text.SimpleDateFormat
import android.util.Log
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
import java.util.Locale

class PlaylistContentViewModel (
private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private lateinit var tracks: List<Track>

    private val _playlistTracks = MutableLiveData<List<Track>>()
    val playlistTracks: LiveData<List<Track>> = _playlistTracks

    private val _tracksCount = MutableLiveData<String>()
    val tracksCount: LiveData<String> = _tracksCount

    private val _playlistDuration = MutableLiveData<String>()
    val playlistDuration: LiveData<String> = _playlistDuration

    fun sumTracksTime(playlistModel: PlaylistModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val duration = SimpleDateFormat("mm", Locale.getDefault())
                .format(playlistInteractor.getPlaylistDuration(playlistModel))

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
                _tracksCount.postValue(getTrackCount(playlist.tracksCount.dec()))
                sumTracksTime(playlist)
            }
        }
    }

    fun deletePlaylist(playlist: PlaylistModel) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.deletePlaylist(playlist)
        }
    }

    fun getTrackCount(count: Int): String {
        val lastDigit = count % 10
        val lastTwoDigits = count % 100

        val ending = when {
            lastDigit == 1 -> "трек"
            lastDigit in 2..4 -> "трека"
            lastTwoDigits in 11..19 -> "треков"
            else -> "треков"
        }

        return "$count $ending"
    }

    fun sharePlaylist(playlist: PlaylistModel): Boolean {
        if (playlist.tracks.isEmpty()) return false
        else {
            var text = """${playlist.name}
${playlist.description}
${getTrackCount(playlist.tracksCount)} """
            var number = 0
            for (track in tracks) {
                number++
                text +="\n$number. ${track.artistName} - ${track.trackName} (${track.trackTime})"
            }
            playlistInteractor.sharePlaylist(text)
            return true
        }
    }

}