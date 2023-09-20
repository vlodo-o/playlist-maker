package com.practicum.playlistmaker.medialib.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.medialib.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrack(track: PlaylistTrackEntity)

    @Query("DELETE FROM ${PlaylistTrackEntity.TABLE_NAME} WHERE trackId = :trackId")
    suspend fun deletePlaylistTrack(trackId: String)

    @Query("SELECT * FROM ${PlaylistTrackEntity.TABLE_NAME} ORDER BY saveDate DESC")
    fun getAllPlaylistTrack(): List<PlaylistTrackEntity>

    @Query("SELECT * FROM ${PlaylistTrackEntity.TABLE_NAME} WHERE trackId = :trackId")
    fun getPlaylistTrack(trackId: String): PlaylistTrackEntity
}