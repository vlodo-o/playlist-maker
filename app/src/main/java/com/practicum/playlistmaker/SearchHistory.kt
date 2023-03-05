package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.model.Track

const val TRACK_SEARCH_HISTORY = "TRACK_SEARCH_HISTORY"

class SearchHistory (val sharedPrefs: SharedPreferences) {

    fun saveHistory(tracks: ArrayList<Track>) {
        val tracksJson = Gson().toJson(tracks)
        sharedPrefs.edit().putString(TRACK_SEARCH_HISTORY, tracksJson).apply()
    }

    fun getHistory(): Array<Track> {
        val tracksJson = sharedPrefs.getString(TRACK_SEARCH_HISTORY, null) ?: return emptyArray()
        return Gson().fromJson(tracksJson, Array<Track>::class.java)
    }

}