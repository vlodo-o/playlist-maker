package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.activity.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.activity.TrackListAdapter
import com.practicum.playlistmaker.search.ui.models.SearchViewState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModel<SearchViewModel>()

    private val trackListAdapter = TrackListAdapter { trackClickListener(it) }
    private val trackHistoryAdapter = TrackListAdapter { trackClickListener(it) }

    private var searchText = ""

    private lateinit var toolbar: Toolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var searchEditText: EditText
    private lateinit var clearTextButton: ImageView
    private lateinit var trackListRecyclerView: RecyclerView
    private lateinit var trackHistoryRecyclerView: RecyclerView
    private lateinit var searchHistoryLayout: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var errorTextView: TextView
    private lateinit var errorImage: ImageView
    private lateinit var refreshButton: Button

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private val searchRunnable = Runnable { viewModel.searchTracks(searchText) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initListeners()
        initSearchEditTextListeners()

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        trackListRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        trackListRecyclerView.adapter = trackListAdapter
        trackHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        trackHistoryRecyclerView.adapter = trackHistoryAdapter

        searchEditText.requestFocus()

        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
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
        toolbar = binding.toolbar
        progressBar = binding.progressBar
        clearTextButton = binding.clearTextButton
        searchEditText = binding.searchEditText
        trackListRecyclerView = binding.trackListRecyclerView
        trackHistoryRecyclerView = binding.trackHistoryRecyclerView
        errorTextView = binding.errorText
        errorImage = binding.errorImage
        refreshButton = binding.refreshButton
        searchHistoryLayout = binding.searchHistoryLayout
        clearHistoryButton = binding.clearHistoryButton
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

            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
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
            searchHistoryLayout.visibility = if (hasFocus && searchEditText.text.isEmpty() && trackHistoryAdapter.trackList.isNotEmpty()) View.VISIBLE else View.GONE
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
            searchHistoryLayout.visibility = View.VISIBLE
        }
        else {
            searchHistoryLayout.visibility = View.GONE
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        searchHistoryLayout.visibility = View.GONE
        trackListRecyclerView.visibility = View.GONE
        errorImage.visibility = View.GONE
        errorTextView.visibility = View.GONE
        refreshButton.visibility = View.GONE
    }

    private fun showSearchResult(tracks: List<Track>) {
        searchHistoryLayout.visibility = View.GONE
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
            val intent = Intent(activity, PlayerActivity::class.java).putExtra(PlayerActivity.TRACK, track)
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

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val LIST_VISIBILITY = "LIST_VISIBILITY"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        fun newInstance() = SearchFragment()
    }

}