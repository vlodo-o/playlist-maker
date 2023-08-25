package com.practicum.playlistmaker.search.ui.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed class SearchViewState {
    class SearchHistory(val tracks: List<Track>): SearchViewState()
    class SearchedTracks(val tracks: List<Track>): SearchViewState()
    class SearchError(val error: com.practicum.playlistmaker.search.data.network.NetworkError) : SearchViewState()
    object Loading : SearchViewState()
}