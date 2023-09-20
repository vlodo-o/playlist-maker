package com.practicum.playlistmaker.medialib.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.medialib.domain.PlaylistInteractor
import com.practicum.playlistmaker.medialib.domain.PlaylistRepository
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
): PlaylistInteractor {

    override suspend fun createPlaylist(name: String, description: String, imagePath: String) {
        playlistRepository.createPlaylist(name, description, imagePath)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        playlistRepository.deletePlaylist(playlistId)
    }

    override suspend fun getPlaylists(): Flow<List<PlaylistModel>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(playlist: PlaylistModel, track: Track) {
        playlistRepository.addTrackToPlaylist(playlist, track)
    }

    override suspend fun saveImageToPrivateStorage(uri: Uri, name: String): Uri {
        return playlistRepository.saveImageToPrivateStorage(uri, name)
    }

    override fun isTrackInPlaylist(playlist: PlaylistModel, track: Track): Boolean {
        return playlistRepository.isTrackInPlaylist(playlist, track)
    }

    override suspend fun getAllPlaylistTracks(playlistId: Int): Flow<List<Track>> {
        return playlistRepository.getAllPlaylistTracks(playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(playlist: PlaylistModel, trackId: String) {
        playlistRepository.deleteTrackFromPlaylist(playlist, trackId)
    }

    override suspend fun getPlaylistDuration(playlist: PlaylistModel): Int {
        return playlistRepository.getPlaylistDuration(playlist)
    }
}