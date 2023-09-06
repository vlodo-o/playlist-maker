package com.practicum.playlistmaker.medialib.data.converters

import com.practicum.playlistmaker.medialib.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.models.Track
import java.util.Calendar

class TrackDbConverter {

    fun map(track: Track) =
        TrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            saveDate = Calendar.getInstance().timeInMillis)

    fun map(track: TrackEntity) =
        Track(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
}