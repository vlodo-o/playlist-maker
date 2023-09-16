package com.practicum.playlistmaker.medialib.domain

import android.net.Uri
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createPlaylist(name: String, description: String, imagePath: String)

    suspend fun deletePlaylist(playlistId: Int)

    suspend fun getPlaylists(): Flow<List<PlaylistModel>>

    suspend fun updatePlaylist(playlist: PlaylistModel)

    suspend fun saveImageToPrivateStorage(uri: Uri, name: String)
}