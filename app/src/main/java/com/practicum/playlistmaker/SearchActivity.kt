package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
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

    private lateinit var toolbar: Toolbar

    private lateinit var searchEditText: EditText
    private var searchText = ""

    private lateinit var clearButton: ImageView

    private lateinit var trackListRecyclerView: RecyclerView
    private val tracks = ArrayList<Track>()
    private val trackListAdapter = TrackListAdapter { trackClickListener(it) }

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

        trackListAdapter.trackList = tracks
        trackListRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackListRecyclerView.adapter = trackListAdapter

        setSupportActionBar(toolbar)
        searchEditText.requestFocus()

        searchEditTextListener()
        clearButtonListener()
        refreshButtonListener()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        clearButton = findViewById(R.id.clear_text_button)
        searchEditText = findViewById(R.id.search_edit_text)
        trackListRecyclerView = findViewById(R.id.track_list_recycler_view)
        errorTextView = findViewById(R.id.error_text)
        errorImage = findViewById(R.id.error_image)
        refreshButton = findViewById(R.id.refresh_button)
    }

    private fun searchEditTextListener() {
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchText = s.toString()
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
    }

    private fun clearButtonListener() {
        clearButton.setOnClickListener {
            searchEditText.text?.clear()
            trackListRecyclerView.visibility = View.VISIBLE
            tracks.clear()
            trackListAdapter.notifyDataSetChanged()

            val view = this.currentFocus
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            if (view != null) {
                inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
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

    private fun searchTracks(query: String) {
        iTunesSearch.getTrack(query).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(call: Call<TracksResponse>,
                                    response: Response<TracksResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        if (response.body()?.tracks?.isNotEmpty() == true) {
                            tracks.clear()
                            tracks.addAll(response.body()?.tracks!!)
                            trackListAdapter.notifyDataSetChanged()
                            showSearchResult(SearchStatus.SUCCESS)
                        }
                        else {
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

    private fun showSearchResult(status: SearchStatus) {
        when(status) {
            SearchStatus.SUCCESS -> {
                trackListRecyclerView.visibility = View.VISIBLE
            }
            SearchStatus.EMPTY_SEARCH -> {
                trackListRecyclerView.visibility = View.GONE
                refreshButton.visibility = View.GONE
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

    private fun trackClickListener(track: Track) {
        Toast.makeText(applicationContext, track.trackName + " was clicked", Toast.LENGTH_LONG).show()
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
    }
}