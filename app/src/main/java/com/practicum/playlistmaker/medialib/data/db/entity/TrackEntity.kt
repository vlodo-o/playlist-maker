package com.practicum.playlistmaker.medialib.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.practicum.playlistmaker.medialib.data.db.entity.TrackEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class TrackEntity (
    @PrimaryKey(autoGenerate = false)
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) {
    companion object {
        const val TABLE_NAME = "favorite_tracks"
    }
}