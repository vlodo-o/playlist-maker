package com.practicum.playlistmaker.medialib.data.converters

import com.practicum.playlistmaker.medialib.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import java.util.Calendar

class PlaylistDbConverter {

    fun map(value: PlaylistEntity): PlaylistModel {
        return PlaylistModel(
            id = value.id,
            name = value.name,
            description = value.description,
            imagePath = value.imagePath,
            tracks = value.tracks,
            tracksCount = value.tracksCount)
    }

    fun map(value: PlaylistModel): PlaylistEntity {
        return PlaylistEntity(
            id = value.id,
            name = value.name,
            description = value.description,
            imagePath = value.imagePath,
            tracks = value.tracks,
            tracksCount = value.tracksCount,
            saveDate = Calendar.getInstance().timeInMillis
        )
    }
}