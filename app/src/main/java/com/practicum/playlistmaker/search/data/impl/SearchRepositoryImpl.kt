package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.data.storage.SearchHistoryStorage
import com.practicum.playlistmaker.search.domain.SearchRepository
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.models.NetworkError

class SearchRepositoryImpl (val networkClient: NetworkClient, val storage: SearchHistoryStorage):
    SearchRepository {

    override fun searchTracks(query: String, onSuccess: (List<Track>) -> Unit, onError: (NetworkError) -> Unit) {
        networkClient.doRequest(query, onSuccess, onError)
    }

    override fun getHistory(): List<Track> {
        return storage.getHistory()
    }

    override fun saveHistory(tracks: List<Track>) {
        storage.saveHistory(tracks)
    }

}