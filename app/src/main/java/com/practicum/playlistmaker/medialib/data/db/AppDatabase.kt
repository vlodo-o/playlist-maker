package com.practicum.playlistmaker.medialib.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.practicum.playlistmaker.medialib.data.converters.TrackListConverter
import com.practicum.playlistmaker.medialib.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.medialib.data.db.dao.PlaylistTrackDao
import com.practicum.playlistmaker.medialib.data.db.dao.TrackDao
import com.practicum.playlistmaker.medialib.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialib.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.medialib.data.db.entity.TrackEntity

@Database(version = 3, entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class])
@TypeConverters(TrackListConverter::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}