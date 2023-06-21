package com.practicum.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.ui.models.SearchViewState

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
) : ViewModel() {

    private val historyList = ArrayList<Track>()

    private val _stateLiveData = MutableLiveData<SearchViewState>()
    val stateLiveData: LiveData<SearchViewState> = _stateLiveData

    init {
        historyList.addAll(searchInteractor.getHistory())
        _stateLiveData.value = SearchViewState.SearchHistory(historyList)
    }

    override fun onCleared() {
        super.onCleared()
        searchInteractor.saveHistory(historyList)
    }

    fun searchTracks(query: String) {
        if (query.isEmpty()) return

        _stateLiveData.value = SearchViewState.Loading

        searchInteractor.searchTracks(query,
            onSuccess = { trackList ->
                _stateLiveData.value = SearchViewState.SearchedTracks(trackList)
            },
            onError = { error ->
                _stateLiveData.value = SearchViewState.SearchError(error)
            }
        )
    }

    fun clearHistory() {
        historyList.clear()
        _stateLiveData.value = SearchViewState.SearchHistory(historyList)
    }

    fun clearSearchText() {
        _stateLiveData.value = SearchViewState.SearchHistory(historyList)
    }

    fun addTrackToHistory(track: Track) {
        if (historyList.contains(track)) {
            historyList.removeAt(historyList.indexOf(track))
        } else if (historyList.size == MAX_HISTORY_SIZE) {
            historyList.removeAt(0)
        }
        historyList.add(track)
        searchInteractor.saveHistory(historyList)
    }

    fun searchFocusChanged(hasFocus: Boolean, text: String) {
        if (hasFocus && text.isEmpty()) {
            _stateLiveData.value = SearchViewState.SearchHistory(historyList)
        }
    }

    companion object {
        private const val MAX_HISTORY_SIZE = 10
    }

}