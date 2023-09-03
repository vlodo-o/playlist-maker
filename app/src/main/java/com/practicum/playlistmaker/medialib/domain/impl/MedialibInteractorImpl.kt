package com.practicum.playlistmaker.medialib.domain.impl

import com.practicum.playlistmaker.medialib.domain.MedialibInteractor
import com.practicum.playlistmaker.medialib.domain.MedialibRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class MedialibInteractorImpl(
    private val medialibRepository: MedialibRepository
): MedialibInteractor {

    override suspend fun saveTrack(track: Track) {
        medialibRepository.saveTrack(track)
    }

    override suspend fun deleteTrack(trackId: String) {
        medialibRepository.deleteTrack(trackId)
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> {
        return medialibRepository.getFavoriteTracks()
    }

    override suspend fun isFavoriteTrack(trackId: String): Flow<Boolean> {
        return medialibRepository.isFavoriteTrack(trackId)
    }
}