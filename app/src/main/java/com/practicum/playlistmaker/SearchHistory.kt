package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.player.domain.models.Track

const val TRACK_SEARCH_HISTORY = "TRACK_SEARCH_HISTORY"

class SearchHistory (private val sharedPrefs: SharedPreferences) {

    fun saveHistory(tracks: ArrayList<Track>) {
        val tracksJson = Gson().toJson(tracks)
        sharedPrefs.edit().putString(TRACK_SEARCH_HISTORY, tracksJson).apply()
    }

    fun getHistory(): Array<Track> {
        val tracksJson = sharedPrefs.getString(TRACK_SEARCH_HISTORY, null) ?: return emptyArray()
        return Gson().fromJson(tracksJson, Array<Track>::class.java)
    }

    fun clearHistory() {
        sharedPrefs.edit().clear().apply()
    }

    fun putTrack(track: Track): ArrayList<Track> {
        val tracks = ArrayList<Track>()
        tracks.addAll(getHistory())

        if (tracks.contains(track)) {
            tracks.removeAt(tracks.indexOf(track))
        } else if (tracks.size == 10) {
            tracks.removeAt(0)
        }

        tracks.add(track)
        saveHistory(tracks)
        return tracks
    }

}