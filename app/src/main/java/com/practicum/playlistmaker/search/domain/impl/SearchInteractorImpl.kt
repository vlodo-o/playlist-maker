package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.SearchRepository
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.SearchResult
import kotlinx.coroutines.flow.Flow

class SearchInteractorImpl(val repository: SearchRepository): SearchInteractor {

    override fun searchTracks(query: String): Flow<SearchResult> {
        return repository.searchTracks(query)
    }

    override fun getHistory(): List<Track> {
        return repository.getHistory().toList()
    }

    override fun saveHistory(tracks: List<Track>) {
        repository.saveHistory(tracks)
    }

}