package com.practicum.playlistmaker.search.domain.models

import com.practicum.playlistmaker.search.data.network.NetworkError

sealed class SearchResult(
    val data: List<Track>? = null,
    val error:NetworkError? = null
) {
    class Success(data: List<Track>): SearchResult(data = data)
    class Error(error: NetworkError): SearchResult(error = error)
}