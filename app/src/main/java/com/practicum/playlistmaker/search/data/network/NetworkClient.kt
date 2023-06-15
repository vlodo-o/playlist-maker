package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.NetworkError

interface NetworkClient {
    fun doRequest(query: String, onSuccess: (List<Track>) -> Unit, onError: (NetworkError) -> Unit)
}