package com.practicum.playlistmaker.medialib.domain

import android.net.Uri
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow


interface PlaylistRepository {

    suspend fun createPlaylist(name: String, description: String, imagePath: String)

    suspend fun deletePlaylist(playlistId: Int)

    suspend fun getPlaylists(): Flow<List<PlaylistModel>>

    suspend fun addTrackToPlaylist(playlist: PlaylistModel, track: Track)

    suspend fun saveImageToPrivateStorage(uri: Uri, name: String): Uri

    fun isTrackInPlaylist(playlist: PlaylistModel, track: Track): Boolean

    suspend fun getAllPlaylistTracks(playlistId: Int): Flow<List<Track>>
}