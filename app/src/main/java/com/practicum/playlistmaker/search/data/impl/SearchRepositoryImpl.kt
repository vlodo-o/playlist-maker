package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.data.storage.SearchHistoryStorage
import com.practicum.playlistmaker.search.domain.SearchRepository
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.NetworkError
import com.practicum.playlistmaker.search.data.network.TracksResponse
import com.practicum.playlistmaker.search.domain.models.SearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl (val networkClient: NetworkClient, val storage: SearchHistoryStorage):
    SearchRepository {

    override fun searchTracks(query: String): Flow<SearchResult> = flow {
        val response = networkClient.doRequest(query)
        when (response.resultCode) {
            -1 -> {
                emit(SearchResult.Error(error = NetworkError.CONNECTION_ERROR))
            }
            200 -> {
                val tracksList = (response as TracksResponse).results
                if (tracksList.isEmpty())
                    emit(SearchResult.Error(error = NetworkError.EMPTY_RESULT))
                else {
                    emit(SearchResult.Success(data = tracksList))
                }
            }
            else -> {
                emit(SearchResult.Error(error = NetworkError.SERVER_ERROR))
            }
        }
    }

    override fun getHistory(): List<Track> {
        return storage.getHistory()
    }

    override fun saveHistory(tracks: List<Track>) {
        storage.saveHistory(tracks)
    }

}