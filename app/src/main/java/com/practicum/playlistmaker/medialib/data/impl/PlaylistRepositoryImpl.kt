package com.practicum.playlistmaker.medialib.data.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.practicum.playlistmaker.medialib.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.medialib.data.converters.PlaylistTrackDbConverter
import com.practicum.playlistmaker.medialib.data.converters.TrackListConverter
import com.practicum.playlistmaker.medialib.data.db.AppDatabase
import com.practicum.playlistmaker.medialib.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.medialib.domain.PlaylistRepository
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val playlistTrackDbConverter: PlaylistTrackDbConverter,
    private val trackListConverter: TrackListConverter,
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

    override suspend fun deletePlaylist(playlist: PlaylistModel) {
        appDatabase.playlistDao().deletePlaylist(playlist.id)
    }

    override suspend fun getPlaylists(): Flow<List<PlaylistModel>> = flow {
        val playlists = appDatabase.playlistDao().getAllPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun addTrackToPlaylist(playlist: PlaylistModel, track: Track) {
        playlist.tracks.add(track.trackId)
        playlist.tracksCount += 1
        updatePlaylist(playlist)
        appDatabase.playlistTrackDao().insertPlaylistTrack(playlistTrackDbConverter.map(track))
    }

    override suspend fun saveImageToPrivateStorage(uri: Uri, name: String): Uri {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"playlist_maker")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, name)
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = withContext(Dispatchers.IO) {
            FileOutputStream(file)
        }
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return Uri.fromFile(file)
    }

    override fun isTrackInPlaylist(playlist: PlaylistModel, track: Track): Boolean {
        return playlist.tracks.contains(track.trackId)
    }

    override suspend fun getAllPlaylistTracks(playlistId: Int): Flow<List<Track>> = flow {
        val trackIds = trackListConverter.jsonToList(appDatabase.playlistDao().getAllPlaylistTracksId(playlistId))
        val resultTracks: ArrayList<Track> = arrayListOf()
        trackIds.forEach { id ->
            val track = appDatabase.playlistTrackDao().getPlaylistTrack(id)
            if (track != null) resultTracks.add(playlistTrackDbConverter.map(track))
        }
        emit(resultTracks)
    }

    override suspend fun deleteTrackFromPlaylist(playlist: PlaylistModel, trackId: String) {
        playlist.tracks.remove(trackId)
        playlist.tracksCount.dec()
        val playlists = appDatabase.playlistDao().updatePlaylistAndGetAll(playlistDbConverter.map(playlist))
        if (!trackInPlaylists(playlists, trackId)) appDatabase.playlistTrackDao().deletePlaylistTrack(trackId)
    }

    override suspend fun getPlaylistDuration(playlist: PlaylistModel): Int {
        val trackList = playlist.tracks.map { appDatabase.playlistTrackDao().getPlaylistTrack(it) }
        var timeSum = 0
        for (track in trackList) {
            timeSum += track.trackTimeMillis
        }
        return timeSum
    }

    override suspend fun updatePlaylist(playlist: PlaylistModel) {
        appDatabase.playlistDao().updatePlaylist(playlistDbConverter.map(playlist))
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<PlaylistModel> {
        return playlists.map {playlist -> playlistDbConverter.map(playlist)}
    }

    private fun trackInPlaylists(playlistList: List<PlaylistEntity>, trackId: String): Boolean {
        for (playlist in playlistList) {
            if (playlist.tracks.contains(trackId)) return true
        }
        return false
    }
}