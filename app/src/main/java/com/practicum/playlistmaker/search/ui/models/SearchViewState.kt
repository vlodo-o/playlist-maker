package com.practicum.playlistmaker.search.ui.models

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.NetworkError

sealed class SearchViewState {
    class SearchHistory(val tracks: List<Track>): SearchViewState()
    class SearchedTracks(val tracks: List<Track>): SearchViewState()
    class SearchError(val error: NetworkError) : SearchViewState()
    object Loading : SearchViewState()
}