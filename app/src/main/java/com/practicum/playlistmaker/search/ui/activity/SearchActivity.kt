package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.player.ui.activity.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.search.ui.models.SearchViewState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private val searchRunnable = Runnable { viewModel.searchTracks(searchText) }

    private lateinit var toolbar: Toolbar

    private lateinit var progressBar: ProgressBar

    private lateinit var searchEditText: EditText
    private var searchText = ""
    private lateinit var clearTextButton: ImageView

    private lateinit var trackListRecyclerView: RecyclerView
    private lateinit var trackHistoryRecyclerView: RecyclerView
    private val trackListAdapter = TrackListAdapter { trackClickListener(it) }
    private val trackHistoryAdapter = TrackListAdapter { trackClickListener(it) }

    private lateinit var historyLayout: LinearLayout
    private lateinit var clearHistoryButton: Button

    private lateinit var errorTextView: TextView
    private lateinit var errorImage: ImageView
    private lateinit var refreshButton: Button

    private val viewModel by viewModel<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        initListeners()
        initSearchEditTextListeners()

        trackListRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackListRecyclerView.adapter = trackListAdapter
        trackHistoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        trackHistoryRecyclerView.adapter = trackHistoryAdapter

        setSupportActionBar(toolbar)
        searchEditText.requestFocus()

        viewModel.stateLiveData.observe(this) { state ->
            when(state) {
                is SearchViewState.SearchHistory -> {
                    showHistoryList(state.tracks)
                }
                is SearchViewState.Loading -> {
                    showLoading()
                }
                is SearchViewState.SearchedTracks -> {
                    showSearchResult(state.tracks)
                }
                is SearchViewState.SearchError -> {
                    showErrorMessage(state.error)
                }
            }
        }

    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progress_bar)
        clearTextButton = findViewById(R.id.clear_text_button)
        searchEditText = findViewById(R.id.search_edit_text)
        trackListRecyclerView = findViewById(R.id.track_list_recycler_view)
        trackHistoryRecyclerView = findViewById(R.id.track_history_recycler_view)
        errorTextView = findViewById(R.id.error_text)
        errorImage = findViewById(R.id.error_image)
        refreshButton = findViewById(R.id.refresh_button)
        historyLayout = findViewById(R.id.search_history_layout)
        clearHistoryButton = findViewById(R.id.clear_history_button)
    }

    private fun initListeners() {
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.searchFocusChanged(hasFocus, searchEditText.text.toString())
        }

        refreshButton.setOnClickListener {
            if (searchText.isNotEmpty()) {
                viewModel.searchTracks(searchText)
            }
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        clearTextButton.setOnClickListener {
            viewModel.clearSearchText()
            searchEditText.text?.clear()
            trackListRecyclerView.visibility = View.GONE
            errorImage.visibility = View.GONE
            errorTextView.visibility = View.GONE
            refreshButton.visibility = View.GONE

            val view = this.currentFocus
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            if (view != null) {
                inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
            }

        }

        refreshButton.setOnClickListener {
            viewModel.searchTracks(searchText)
        }

    }

    private fun initSearchEditTextListeners() {
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearTextButton.visibility = clearButtonVisibility(s)
                searchText = s.toString()
                if (searchText.isEmpty()) {
                    viewModel.clearSearchText()
                }
                else {
                    searchDebounce()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        searchEditText.addTextChangedListener(searchTextWatcher)

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchText = searchEditText.text.toString()
                if (searchText.isNotEmpty()) {
                    viewModel.searchTracks(searchText)
                }
            }
            false
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            historyLayout.visibility = if (hasFocus && searchEditText.text.isEmpty() && trackHistoryAdapter.trackList.isNotEmpty()) View.VISIBLE else View.GONE
        }

    }

    private fun showHistoryList(tracks: List<Track>) {
        trackHistoryAdapter.setTracks(tracks)
        errorImage.visibility = View.GONE
        errorTextView.visibility = View.GONE
        refreshButton.visibility = View.GONE
        trackListRecyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
        if (tracks.isNotEmpty()) {
            historyLayout.visibility = View.VISIBLE
        }
        else {
            historyLayout.visibility = View.GONE
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE
        trackListRecyclerView.visibility = View.GONE
        errorImage.visibility = View.GONE
        errorTextView.visibility = View.GONE
        refreshButton.visibility = View.GONE
    }

    private fun showSearchResult(tracks: List<Track>) {
        historyLayout.visibility = View.GONE
        progressBar.visibility = View.GONE
        trackListRecyclerView.visibility = View.VISIBLE
        trackListAdapter.setTracks(tracks)
    }

    private fun showErrorMessage(error: NetworkError) {
        progressBar.visibility = View.GONE
        trackListRecyclerView.visibility = View.GONE
        when(error) {
            NetworkError.EMPTY_RESULT -> {
                refreshButton.visibility = View.GONE
                errorImage.visibility = View.VISIBLE
                errorTextView.visibility = View.VISIBLE
                errorImage.setImageResource(R.drawable.ic_not_found)
                errorTextView.text = getString(R.string.not_found)
            }
            NetworkError.CONNECTION_ERROR -> {
                refreshButton.visibility = View.VISIBLE
                errorImage.visibility = View.VISIBLE
                errorTextView.visibility = View.VISIBLE
                errorImage.setImageResource(R.drawable.ic_network_error)
                errorTextView.text = getString(R.string.network_error)
            }}
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun trackClickListener(track: Track) {
        if (clickDebounce()) {
            viewModel.addTrackToHistory(track)
            val intent = Intent(this, PlayerActivity::class.java).putExtra(PlayerActivity.TRACK,
                Gson().toJson(track))
            startActivity(intent)
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
        outState.putInt(LIST_VISIBILITY, trackListRecyclerView.visibility)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT).toString()
        searchEditText.setText(searchText)
        val visibility = savedInstanceState.getInt(LIST_VISIBILITY)
        trackListRecyclerView.visibility = visibility
    }


    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val LIST_VISIBILITY = "LIST_VISIBILITY"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}