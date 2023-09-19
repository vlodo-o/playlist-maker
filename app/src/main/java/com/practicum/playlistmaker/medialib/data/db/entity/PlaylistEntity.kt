package com.practicum.playlistmaker.medialib.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.practicum.playlistmaker.medialib.data.db.entity.PlaylistEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val imagePath: String,
    val tracks: MutableList<String> = mutableListOf(),
    val tracksCount: Int,
    val saveDate: Long)
{
    companion object {
        const val TABLE_NAME = "playlist"
    }
}