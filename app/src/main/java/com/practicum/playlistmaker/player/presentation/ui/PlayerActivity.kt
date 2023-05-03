package com.practicum.playlistmaker.player.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.creator.Creator
import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.player.presentation.PlayerView
import com.practicum.playlistmaker.player.presentation.presenter.PlayerPresenter
import java.util.*

class PlayerActivity : AppCompatActivity(), PlayerView {

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

    private lateinit var saveButton: FloatingActionButton
    private lateinit var playButton: FloatingActionButton
    private lateinit var favoriteButton: FloatingActionButton

    private lateinit var track: Track

    private lateinit var presenter: PlayerPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        track = Gson().fromJson((intent.getStringExtra(TRACK)), Track::class.java)
        initViews()
        setTrackInfo()
        presenter = Creator.providePresenter(
            view = this,
            track = track
        )
        initListeners()
    }

    override fun onPause() {
        super.onPause()
        presenter.onViewPaused()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDestroyed()
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
            presenter.playbackControl()
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
    }

    override fun drawTrack(track: Track) {
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
    }

    override fun setPlayIcon() {
        playButton.setImageResource(R.drawable.ic_play)
    }

    override fun setPauseIcon() {
        playButton.setImageResource(R.drawable.ic_pause)
    }

    override fun setDuration(ms: String) {
        trackTimeCountTextView.text = ms
    }

    companion object {
        const val TRACK = "TRACK"
    }
}