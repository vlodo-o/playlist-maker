package com.practicum.playlistmaker.player.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var coverImageView: ImageView
    private lateinit var trackNameTextView: TextView
    private lateinit var trackArtistTextView: TextView
    private lateinit var trackTimeCountTextView: TextView
    private lateinit var trackDurationTextView: TextView
    private lateinit var trackAlbumText: TextView
    private lateinit var trackAlbumTextView: TextView
    private lateinit var trackYearTextView: TextView
    private lateinit var trackGenreTextView: TextView
    private lateinit var trackCountryTextView: TextView

    private lateinit var saveButton: ImageButton
    private lateinit var playButton: FloatingActionButton
    private lateinit var favoriteButton: ImageButton

    private lateinit var track: Track

    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        if (Build.VERSION.SDK_INT >= 33) {
            track = intent.getParcelableExtra(TRACK, Track::class.java)!!
        }
        else {
            @Suppress("DEPRECATION")
            track = intent.getParcelableExtra(TRACK)!!
        }

        initViews()
        initListeners()
        setTrackInfo()

        viewModel.playState.observe(this) { playState ->
            if (playState) {
                playButton.setImageResource(R.drawable.ic_pause)
            } else {
                playButton.setImageResource(R.drawable.ic_play)
            }
        }
        viewModel.playProgress.observe(this) { duration ->
            trackTimeCountTextView.text = duration
        }

        viewModel.favoriteState.observe(this) { isFavorite ->
            if (isFavorite) {
                favoriteButton.setImageResource(R.drawable.favorite_button)
            }
            else {
                favoriteButton.setImageResource(R.drawable.not_favorite_button)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopPlayer()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        coverImageView = findViewById(R.id.cover_imageview)
        trackNameTextView = findViewById(R.id.track_name_textview)
        trackArtistTextView = findViewById(R.id.track_artist_textview)
        trackTimeCountTextView = findViewById(R.id.time_count)
        trackDurationTextView = findViewById(R.id.changeable_duration)
        trackAlbumText = findViewById(R.id.album)
        trackAlbumTextView = findViewById(R.id.changeable_album)
        trackYearTextView = findViewById(R.id.changeable_year)
        trackGenreTextView = findViewById(R.id.changeable_genre)
        trackCountryTextView = findViewById(R.id.changeable_country)
        saveButton = findViewById(R.id.save_button)
        playButton = findViewById(R.id.play_button)
        favoriteButton = findViewById(R.id.favorite_button)
    }

    private fun initListeners() {
        toolbar.setNavigationOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            viewModel.playbackControl(track.previewUrl)
        }

        favoriteButton.setOnClickListener {
            viewModel.favoriteControl(track)
        }
    }

    private fun setTrackInfo() {
        val cornerRadius = this.resources.getDimensionPixelSize(R.dimen.full_track_cover_radius)
        Glide.with(this).load(track.getCoverArtwork())
            .placeholder(R.drawable.track_placeholder).centerCrop()
            .transform(RoundedCorners(cornerRadius)).into(coverImageView)

        trackNameTextView.text = track.trackName
        trackArtistTextView.text = track.artistName
        trackDurationTextView.text = track.trackTime
        if (track.collectionName.isNotEmpty()) {
            trackAlbumTextView.text = track.collectionName
        } else {
            trackAlbumTextView.visibility = View.GONE
            trackAlbumText.visibility = View.GONE
        }
        trackGenreTextView.text = track.primaryGenreName
        trackYearTextView.text = track.releaseYear
        trackCountryTextView.text = track.country

        viewModel.checkFavorite(track.trackId)
    }

    companion object {
        const val TRACK = "track"
    }
}