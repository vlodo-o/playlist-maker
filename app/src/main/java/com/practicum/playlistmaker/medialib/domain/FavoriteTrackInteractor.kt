package com.practicum.playlistmaker.medialib.domain

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackInteractor {

    suspend fun saveTrack(track: Track)

    suspend fun deleteTrack(trackId: String)

    suspend fun getFavoriteTracks(): Flow<List<Track>>

    suspend fun isFavoriteTrack(trackId: String): Flow<Boolean>
}