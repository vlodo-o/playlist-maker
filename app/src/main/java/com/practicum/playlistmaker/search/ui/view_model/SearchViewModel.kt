package com.practicum.playlistmaker.search.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
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
        _stateLiveData.postValue(SearchViewState.SearchHistory(historyList))
    }

    override fun onCleared() {
        super.onCleared()
        searchInteractor.saveHistory(historyList)
    }

    fun searchTracks(query: String) {
        if (query.isEmpty()) return

        _stateLiveData.postValue(SearchViewState.Loading)

        searchInteractor.searchTracks(query,
            onSuccess = { trackList ->
                _stateLiveData.postValue(SearchViewState.SearchedTracks(trackList))
            },
            onError = { error ->
                _stateLiveData.postValue(SearchViewState.SearchError(error))
            }
        )
    }

    fun clearHistory() {
        historyList.clear()
        _stateLiveData.postValue(SearchViewState.SearchHistory(historyList))
    }

    fun clearSearchText() {
        _stateLiveData.postValue(SearchViewState.SearchHistory(historyList))
    }

    fun addTrackToHistory(track: Track) {
        if (historyList.contains(track)) {
            historyList.removeAt(historyList.indexOf(track))
        } else if (historyList.size == maxHistorySize) {
            historyList.removeAt(0)
        }
        historyList.add(track)
        searchInteractor.saveHistory(historyList)
    }

    fun searchFocusChanged(hasFocus: Boolean, text: String) {
        if (hasFocus && text.isEmpty()) {
            _stateLiveData.postValue(SearchViewState.SearchHistory(historyList))
        }
    }

    companion object {

        private const val maxHistorySize = 10

        fun getViewModelFactory(context: Context): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SearchViewModel(
                        Creator.provideSearchInteractor(context)
                    )

                }
            }
    }

}