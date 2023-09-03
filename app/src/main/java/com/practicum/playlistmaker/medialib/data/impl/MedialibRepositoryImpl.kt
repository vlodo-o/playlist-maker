package com.practicum.playlistmaker.medialib.data.impl

import com.practicum.playlistmaker.medialib.data.converters.TrackDbConverter
import com.practicum.playlistmaker.medialib.data.db.AppDatabase
import com.practicum.playlistmaker.medialib.data.db.entity.TrackEntity
import com.practicum.playlistmaker.medialib.domain.MedialibRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MedialibRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
): MedialibRepository {

    override suspend fun saveTrack(track: Track) {
        appDatabase.trackDao().insertTrack(trackDbConverter.map(track))
    }

    override suspend fun deleteTrack(trackId: String) {
        appDatabase.trackDao().deleteTrack(trackId)
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getFavoriteTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override suspend fun isFavoriteTrack(trackId: String): Flow<Boolean> = flow {
        val isFavorite = appDatabase.trackDao().isFavoriteTrack(trackId)
        emit(isFavorite)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map {track -> trackDbConverter.map(track)}
    }
}