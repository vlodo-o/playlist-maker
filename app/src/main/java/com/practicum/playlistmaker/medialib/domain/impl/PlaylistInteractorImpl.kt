package com.practicum.playlistmaker.medialib.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.medialib.domain.PlaylistInteractor
import com.practicum.playlistmaker.medialib.domain.PlaylistRepository
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
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

    override suspend fun updatePlaylist(playlist: PlaylistModel) {
        playlistRepository.updatePlaylist(playlist)
    }

    override suspend fun saveImageToPrivateStorage(uri: Uri, name: String) {
        playlistRepository.saveImageToPrivateStorage(uri, name)
    }
}