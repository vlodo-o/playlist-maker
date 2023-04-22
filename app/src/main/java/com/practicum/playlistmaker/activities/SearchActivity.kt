package com.practicum.playlistmaker.activities

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
import com.practicum.playlistmaker.SearchHistory
import com.practicum.playlistmaker.adapter.TrackListAdapter
import com.practicum.playlistmaker.api.ITunesSearchApi
import com.practicum.playlistmaker.api.TracksResponse
import com.practicum.playlistmaker.model.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private val searchRunnable = Runnable { searchTracks(searchText) }

    private lateinit var toolbar: Toolbar

    private lateinit var progressBar: ProgressBar

    private lateinit var searchEditText: EditText
    private var searchText = ""
    private lateinit var clearButton: ImageView

    private lateinit var trackListRecyclerView: RecyclerView
    private lateinit var trackHistoryRecyclerView: RecyclerView
    private val trackListAdapter = TrackListAdapter { trackClickListener(it) }
    private val trackHistoryAdapter = TrackListAdapter { trackClickListener(it) }

    private lateinit var searchHistory: SearchHistory
    private lateinit var historyLayout: LinearLayout
    private lateinit var clearHistoryButton: Button

    private lateinit var errorTextView: TextView
    private lateinit var errorImage: ImageView
    private lateinit var refreshButton: Button

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesSearch = retrofit.create(ITunesSearchApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()

        trackListRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackListRecyclerView.adapter = trackListAdapter

        val sharedPrefs = getSharedPreferences(SEARCH_TRACK_HISTORY, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)
        trackHistoryAdapter.trackList.addAll(searchHistory.getHistory())

        if (trackHistoryAdapter.trackList.isNotEmpty()) {
            historyLayout.visibility = View.VISIBLE
        }

        trackHistoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        trackHistoryRecyclerView.adapter = trackHistoryAdapter

        setSupportActionBar(toolbar)
        searchEditText.requestFocus()

        searchEditTextListener()
        clearButtonListener()
        refreshButtonListener()
        clearHistoryButtonListener()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progress_bar)
        clearButton = findViewById(R.id.clear_text_button)
        searchEditText = findViewById(R.id.search_edit_text)
        trackListRecyclerView = findViewById(R.id.track_list_recycler_view)
        trackHistoryRecyclerView = findViewById(R.id.track_history_recycler_view)
        errorTextView = findViewById(R.id.error_text)
        errorImage = findViewById(R.id.error_image)
        refreshButton = findViewById(R.id.refresh_button)
        historyLayout = findViewById(R.id.search_history_layout)
        clearHistoryButton = findViewById(R.id.clear_history_button)
    }

    private fun searchEditTextListener() {
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchText = s.toString()
                historyLayout.visibility = if (searchEditText.hasFocus() && s?.isEmpty() == true && trackHistoryAdapter.trackList.isNotEmpty()) View.VISIBLE else View.GONE
                searchDebounce()
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
                    searchTracks(searchText)
                }
            }
            false
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            historyLayout.visibility = if (hasFocus && searchEditText.text.isEmpty() && trackHistoryAdapter.trackList.isNotEmpty()) View.VISIBLE else View.GONE
        }

    }

    private fun clearButtonListener() {
        clearButton.setOnClickListener {
            searchEditText.text?.clear()
            trackListRecyclerView.visibility = View.GONE
            errorImage.visibility = View.GONE
            errorTextView.visibility = View.GONE
            refreshButton.visibility = View.GONE
            trackListAdapter.setTracks(null)

            val view = this.currentFocus
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            if (view != null) {
                inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
            }

            if (searchHistory.getHistory().isNotEmpty()) {
                historyLayout.visibility = View.VISIBLE

            }

        }
    }

    private fun refreshButtonListener() {
        refreshButton.setOnClickListener {
            if (searchText.isNotEmpty()) {
                searchTracks(searchText)
            }
        }
    }

    private fun clearHistoryButtonListener() {
        clearHistoryButton.setOnClickListener {
            trackHistoryAdapter.setTracks(null)
            searchHistory.clearHistory()
            historyLayout.visibility = View.GONE
        }
    }

    private fun searchTracks(query: String) {
        trackListRecyclerView.visibility = View.GONE
        errorImage.visibility = View.GONE
        errorTextView.visibility = View.GONE
        refreshButton.visibility = View.GONE
        if (query.isNotEmpty()) {
            historyLayout.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            iTunesSearch.getTrack(query).enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.tracks?.isNotEmpty() == true) {
                                trackListAdapter.setTracks(response.body()?.tracks!!)
                                showSearchResult(SearchStatus.SUCCESS)
                            } else {
                                showSearchResult(SearchStatus.EMPTY_SEARCH)
                            }
                        }
                        else ->
                            showSearchResult(SearchStatus.CONNECTION_ERROR)
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    showSearchResult(SearchStatus.CONNECTION_ERROR)
                }

            })
        }
    }

    private fun showSearchResult(status: SearchStatus) {
        progressBar.visibility = View.GONE
        when(status) {
            SearchStatus.SUCCESS -> {
                trackListRecyclerView.visibility = View.VISIBLE
            }
            SearchStatus.EMPTY_SEARCH -> {
                trackListRecyclerView.visibility = View.GONE
                refreshButton.visibility = View.GONE
                errorImage.visibility = View.VISIBLE
                errorTextView.visibility = View.VISIBLE
                errorImage.setImageResource(R.drawable.ic_not_found)
                errorTextView.text = getString(R.string.not_found)
            }
            SearchStatus.CONNECTION_ERROR -> {
                trackListRecyclerView.visibility = View.GONE
                refreshButton.visibility = View.VISIBLE
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
            trackHistoryAdapter.setTracks(searchHistory.putTrack(track))
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

    enum class SearchStatus { SUCCESS, CONNECTION_ERROR, EMPTY_SEARCH }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val LIST_VISIBILITY = "LIST_VISIBILITY"
        const val SEARCH_TRACK_HISTORY = "SEARCH_TRACK_HISTORY"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}