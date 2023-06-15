package com.practicum.playlistmaker.search.data.storage

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryStorage {

    fun saveHistory(tracks: List<Track>)

    fun getHistory(): List<Track>

    fun clearHistory()

}