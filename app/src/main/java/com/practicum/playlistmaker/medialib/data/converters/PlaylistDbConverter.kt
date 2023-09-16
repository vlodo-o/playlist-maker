package com.practicum.playlistmaker.medialib.data.converters

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.medialib.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialib.data.db.entity.TrackEntity
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import java.util.Calendar

class PlaylistDbConverter {

    fun map(value: PlaylistEntity): PlaylistModel {
        val trackDbConverter = TrackDbConverter()
        val tracks = value.tracks.map {track -> trackDbConverter.map(track)}
        return PlaylistModel(
            id = value.id,
            name = value.name,
            description = value.description,
            imagePath = value.imagePath,
            tracks = tracks.toMutableList(),
            tracksCount = value.tracksCount)
    }

    fun map(value: PlaylistModel): PlaylistEntity {
        val trackDbConverter = TrackDbConverter()
        val tracks = value.tracks.map {track -> trackDbConverter.map(track)}
        return PlaylistEntity(
            id = value.id,
            name = value.name,
            description = value.description,
            imagePath = value.imagePath,
            tracks = tracks.toMutableList(),
            tracksCount = value.tracksCount,
            saveDate = Calendar.getInstance().timeInMillis
        )
    }
}