package com.practicum.playlistmaker.medialib.domain

import android.net.Uri
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun updatePlaylist(playlist: PlaylistModel)

    suspend fun createPlaylist(name: String, description: String, imagePath: String)

    suspend fun deletePlaylist(playlist: PlaylistModel)

    suspend fun getPlaylists(): Flow<List<PlaylistModel>>

    suspend fun addTrackToPlaylist(playlist: PlaylistModel, track: Track)

    suspend fun saveImageToPrivateStorage(uri: Uri, name: String): Uri

    fun isTrackInPlaylist(playlist: PlaylistModel, track: Track): Boolean

    suspend fun getAllPlaylistTracks(playlistId: Int): Flow<List<Track>>

    suspend fun deleteTrackFromPlaylist(playlist: PlaylistModel, trackId: String)

    suspend fun getPlaylistDuration(playlist: PlaylistModel): Int

    fun sharePlaylist(text: String)
}