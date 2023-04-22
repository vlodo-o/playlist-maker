package com.practicum.playlistmaker.activities

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.model.Track
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var coverImageView: ImageView
    private lateinit var trackNameTextView: TextView
    private lateinit var trackArtistTextView: TextView
    private lateinit var trackTimeCountTextView: TextView
    private lateinit var trackDurationTextView: TextView
    private lateinit var trackAlbumTextView: TextView
    private lateinit var trackYearTextView: TextView
    private lateinit var trackGenreTextView: TextView
    private lateinit var trackCountryTextView: TextView

    private lateinit var saveButton: FloatingActionButton
    private lateinit var playButton: FloatingActionButton
    private lateinit var favoriteButton: FloatingActionButton

    private lateinit var track: Track

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private var mainThreadHandler = Handler(Looper.getMainLooper())
    private val playerTimerRunnable = updateTimer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        track = Gson().fromJson((intent.getStringExtra(TRACK)), Track::class.java)
        initViews()
        preparePlayer()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacks(playerTimerRunnable)
        mediaPlayer.release()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        coverImageView = findViewById(R.id.cover_imageview)
        trackNameTextView = findViewById(R.id.track_name_textview)
        trackArtistTextView = findViewById(R.id.track_artist_textview)
        trackTimeCountTextView = findViewById(R.id.time_count)
        trackDurationTextView = findViewById(R.id.changeable_duration)
        val trackAlbumText: TextView = findViewById(R.id.album)
        trackAlbumTextView = findViewById(R.id.changeable_album)
        trackYearTextView = findViewById(R.id.changeable_year)
        trackGenreTextView = findViewById(R.id.changeable_genre)
        trackCountryTextView = findViewById(R.id.changeable_country)
        saveButton = findViewById(R.id.save_button)
        playButton = findViewById(R.id.play_button)
        favoriteButton = findViewById(R.id.favorite_button)

        toolbar.setNavigationOnClickListener {
            finish()
        }

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

        playButton.setOnClickListener {
            playbackControl()
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        // Изменение кнопки и сброс таймера по завершении воспроизведения
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.ic_play)
            playerState = STATE_PREPARED
            mainThreadHandler.removeCallbacks(playerTimerRunnable)
            trackTimeCountTextView.text = getString(R.string.zero_time)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
        mainThreadHandler.postDelayed(playerTimerRunnable, DURATION_UPDATE_DELAY_MS)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_play)
        playerState = STATE_PAUSED
        mainThreadHandler.removeCallbacks(playerTimerRunnable)
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun updateTimer(): Runnable {
        return object: Runnable {
            override fun run() {
                // Вывод позиции воспроизведения в медиаплеере
                trackTimeCountTextView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                mainThreadHandler.postDelayed(this, DURATION_UPDATE_DELAY_MS)
            }
        }
    }

    companion object {
        const val TRACK = "TRACK"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DURATION_UPDATE_DELAY_MS = 300L
    }
}