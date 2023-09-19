package com.practicum.playlistmaker.medialib.domain.impl

import com.practicum.playlistmaker.medialib.domain.FavoriteTrackInteractor
import com.practicum.playlistmaker.medialib.domain.FavoriteTrackRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTrackInteractorImpl(
    private val favoriteTrackRepository: FavoriteTrackRepository
): FavoriteTrackInteractor {

    override suspend fun saveTrack(track: Track) {
        favoriteTrackRepository.saveTrack(track)
    }

    override suspend fun deleteTrack(trackId: String) {
        favoriteTrackRepository.deleteTrack(trackId)
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTrackRepository.getFavoriteTracks()
    }

    override suspend fun isFavoriteTrack(trackId: String): Flow<Boolean> {
        return favoriteTrackRepository.isFavoriteTrack(trackId)
    }
}