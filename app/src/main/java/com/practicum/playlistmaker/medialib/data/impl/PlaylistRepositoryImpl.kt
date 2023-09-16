package com.practicum.playlistmaker.medialib.data.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.practicum.playlistmaker.medialib.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.medialib.data.db.AppDatabase
import com.practicum.playlistmaker.medialib.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialib.data.db.entity.TrackEntity
import com.practicum.playlistmaker.medialib.domain.PlaylistRepository
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val context: Context
): PlaylistRepository {

    override suspend fun createPlaylist(name: String, description: String, imagePath: String) {
        val playlist = PlaylistEntity(
            name = name,
            description = description,
            imagePath = imagePath,
            tracks = mutableListOf(),
            tracksCount = 0,
            saveDate = Calendar.getInstance().timeInMillis
         )
        appDatabase.playlistDao().insertPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        appDatabase.playlistDao().deletePlaylist(playlistId)
    }

    override suspend fun getPlaylists(): Flow<List<PlaylistModel>> = flow {
        val playlists = appDatabase.playlistDao().getAllPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun updatePlaylist(playlist: PlaylistModel) {
        appDatabase.playlistDao().updatePlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun saveImageToPrivateStorage(uri: Uri, name: String) {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"my_album")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, name)
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<PlaylistModel> {
        return playlists.map {playlist -> playlistDbConverter.map(playlist)}
    }
}