package com.practicum.playlistmaker.medialib.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.medialib.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM ${TrackEntity.TABLE_NAME} WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: String)

    @Query("SELECT * FROM ${TrackEntity.TABLE_NAME} ORDER BY saveDate DESC")
    suspend fun getFavoriteTracks(): List<TrackEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM ${TrackEntity.TABLE_NAME} WHERE trackId = :trackId)")
    suspend fun isFavoriteTrack(trackId: String): Boolean

}