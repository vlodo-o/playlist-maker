package com.practicum.playlistmaker.player.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.medialib.domain.models.PlaylistModel
import com.practicum.playlistmaker.medialib.ui.fragments.playlists.NewPlaylistFragment
import com.practicum.playlistmaker.medialib.ui.fragments.playlists.PlaylistAdapter
import com.practicum.playlistmaker.medialib.ui.models.PlaylistsState
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private var _binding: ActivityPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var onPlaylistClickDebounce: (PlaylistModel) -> Unit
    private val playlistAdapter = PlaylistAdapter { onPlaylistClickDebounce(it) }

    private lateinit var track: Track
    private lateinit var playlistName: String

    private val viewModel by viewModel<PlayerViewModel>()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = intent.getParcelableExtra(TRACK)!!

        viewModel.playNewTrack(track)

        initListeners()
        setTrackInfo()

        onPlaylistClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, this.lifecycleScope, false) { playlist ->
            playlistName = playlist.name
            viewModel.addToPlaylist(playlist, track)
        }

        binding.playlistsRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.playlistsRecyclerview.adapter = playlistAdapter

        viewModel.playState.observe(this) { playState ->
            if (playState) {
                binding.playButton.setImageResource(R.drawable.ic_pause)
            } else {
                binding.playButton.setImageResource(R.drawable.ic_play)
            }
        }
        viewModel.playProgress.observe(this) { duration ->
            binding.timeCount.text = duration
        }

        viewModel.favoriteState.observe(this) { isFavorite ->
            if (isFavorite) {
                binding.favoriteButton.setImageResource(R.drawable.favorite_button)
            }
            else {
                binding.favoriteButton.setImageResource(R.drawable.not_favorite_button)
            }
        }

        viewModel.playlistsState.observe(this) { state ->
            if (state is PlaylistsState.Playlists) {
                playlistAdapter.setPlaylists(state.content)
                binding.playlistsRecyclerview.visibility = View.VISIBLE
            } else {
                binding.playlistsRecyclerview.visibility = View.GONE
            }
        }

        viewModel.trackAddedToPlaylist.observe(this) { result ->
            if (result) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.overlay.visibility = View.GONE
                Toast.makeText(this, "Добавлено в плейлист $playlistName", Toast.LENGTH_SHORT).show()
                viewModel.getPlaylists()
            } else {
                Toast.makeText(this, "Трек уже добавлен в плейлист $playlistName", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getPlaylists()
    }

    private fun initListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.playButton.setOnClickListener {
            ensureNotificationPermission {
                viewModel.playbackControl(track.previewUrl)
            }
        }

        binding.favoriteButton.setOnClickListener {
            viewModel.favoriteControl(track)
        }

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.playlists_bottom_sheet)).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.saveButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.overlay.visibility = View.VISIBLE
        }

        binding.newPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                addToBackStack(null)
                add(R.id.create_playlist_fragment, NewPlaylistFragment())
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset + 1
            }
        })

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                binding.playerScrollview.visibility = View.GONE
                binding.overlay.visibility = View.GONE
                binding.playlistsBottomSheet.visibility = View.GONE
                binding.createPlaylistFragment.visibility = View.VISIBLE
            } else {
                viewModel.getPlaylists()
                binding.playerScrollview.visibility = View.VISIBLE
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.overlay.visibility = View.GONE
                } else binding.overlay.visibility = View.VISIBLE
                binding.playlistsBottomSheet.visibility = View.VISIBLE
                binding.createPlaylistFragment.visibility = View.GONE
            }
        }
    }

    private fun setTrackInfo() {
        val cornerRadius = this.resources.getDimensionPixelSize(R.dimen.corner_radius)
        Glide.with(this).load(track.getCoverArtwork())
            .placeholder(R.drawable.track_placeholder).centerCrop()
            .transform(RoundedCorners(cornerRadius)).into(binding.coverImageview)

        binding.trackNameTextview.text = track.trackName
        binding.trackArtistTextview.text = track.artistName
        binding.changeableDuration.text = track.trackTime
        if (track.collectionName.isNotEmpty()) {
            binding.changeableAlbum.text = track.collectionName
        } else {
            binding.changeableAlbum.visibility = View.GONE
            binding.changeableAlbum.visibility = View.GONE
        }
        binding.changeableGenre.text = track.primaryGenreName
        binding.changeableYear.text = track.releaseYear
        binding.changeableCountry.text = track.country

        viewModel.checkFavorite(track.trackId)
    }

    private fun ensureNotificationPermission(onGranted: () -> Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                onGranted()
            } else {
                androidx.core.app.ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATIONS
                )
            }
        } else {
            onGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                viewModel.playbackControl(track.previewUrl)
            } else {
                Toast.makeText(this, "Разрешите уведомления, чтобы управлять плеером в фоне", Toast.LENGTH_SHORT).show()
                // можно всё равно играть без уведомления:
                viewModel.playbackControl(track.previewUrl)
            }
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val TRACK = "track"
        private const val REQUEST_CODE_NOTIFICATIONS = 1001
    }
}