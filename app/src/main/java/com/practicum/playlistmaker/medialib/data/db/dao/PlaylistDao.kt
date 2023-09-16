package com.practicum.playlistmaker.medialib.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.medialib.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM ${PlaylistEntity.TABLE_NAME} WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Int)

    @Query("SELECT * FROM ${PlaylistEntity.TABLE_NAME} ORDER BY saveDate DESC")
    fun getAllPlaylists(): List<PlaylistEntity>

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

}