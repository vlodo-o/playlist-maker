package com.practicum.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.SearchResult
import com.practicum.playlistmaker.search.ui.models.SearchViewState
import com.practicum.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
) : ViewModel() {

    private val historyList = ArrayList<Track>()

    private val _stateLiveData = MutableLiveData<SearchViewState>()
    val stateLiveData: LiveData<SearchViewState> = _stateLiveData

    private var latestSearchText: String? = null

    private val trackSearchDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
        loadTracks(changedText)
    }

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

        if (latestSearchText != query) {
            latestSearchText = query
            trackSearchDebounce(query)
        }
    }

    private fun loadTracks(query: String) {
        _stateLiveData.value = SearchViewState.Loading

        viewModelScope.launch {
            searchInteractor
                .searchTracks(query)
                .collect { result ->
                    when (result) {
                        is SearchResult.Success -> {
                            _stateLiveData.value = SearchViewState.SearchedTracks(result.data!!)
                        }
                        is SearchResult.Error -> {
                            _stateLiveData.value = SearchViewState.SearchError(result.error!!)
                        }
                    }
                }
        }
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
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

}