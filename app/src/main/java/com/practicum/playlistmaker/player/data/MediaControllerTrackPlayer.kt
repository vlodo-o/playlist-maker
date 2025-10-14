package com.practicum.playlistmaker.player.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.practicum.playlistmaker.player.domain.api.TrackPlayer
import androidx.media3.session.SessionToken
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class MediaControllerTrackPlayer(
    context: Context,
    private val mediaBrowserServiceComponent: ComponentName
) : TrackPlayer {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var controller: MediaController? = null

    private val _playerStateFlow = MutableStateFlow(PlayerState.DEFAULT)
    override val playerStateFlow: StateFlow<PlayerState> = _playerStateFlow.asStateFlow()

    private val _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean> = _isConnected

    init {
        val intent = Intent(appContext, MusicService::class.java)
        ContextCompat.startForegroundService(appContext, intent)

        scope.launch {
            try {
                val token = SessionToken(appContext, mediaBrowserServiceComponent)
                val controllerFuture = MediaController.Builder(appContext, token).buildAsync()
                controller = suspendCancellableCoroutine { cont ->
                    controllerFuture.addListener({
                        try {
                            val c = controllerFuture.get()
                            cont.resume(c)
                            observePlayerState(c)
                        } catch (e: Exception) {
                            cont.cancel(e)
                        }
                    }, Dispatchers.Main.asExecutor())
                }
                _isConnected.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun playNewTrack(track: Track) {
        if (controller != null) {
            setMediaController(track)
        } else {
            isConnected.observeForever(object : Observer<Boolean> {
                override fun onChanged(value: Boolean) {
                    if (value) {
                        isConnected.removeObserver(this)
                        setMediaController(track)
                    }
                }
            })
        }
    }

    fun setMediaController(track: Track) {
        controller?.apply {
            stop()
            clearMediaItems()

            val mediaMetadata = MediaMetadata.Builder()
                .setTitle(track.trackName)
                .setArtist(track.artistName)
                .setAlbumTitle(track.collectionName)
                .setArtworkUri(track.artworkUrl100.toUri())
                .build()

            val mediaItem = MediaItem.Builder()
                .setUri(track.previewUrl)
                .setMediaMetadata(mediaMetadata)
                .build()

            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }
    private fun observePlayerState(controller: MediaController) {
        controller.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerStateFlow.value = if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_IDLE -> _playerStateFlow.value = PlayerState.DEFAULT
                    Player.STATE_BUFFERING -> _playerStateFlow.value = PlayerState.PREPARED
                    Player.STATE_READY -> {
                        if (controller.isPlaying) {
                            _playerStateFlow.value = PlayerState.PLAYING
                        } else {
                            _playerStateFlow.value = PlayerState.PAUSED
                        }
                    }
                    Player.STATE_ENDED -> _playerStateFlow.value = PlayerState.DEFAULT
                }
            }

        })
    }

    override fun start(trackUrl: String) {
        controller?.apply {
            if (currentMediaItem?.localConfiguration?.uri.toString() != trackUrl) {
                setMediaItem(MediaItem.fromUri(trackUrl))
                prepare()
            }
            play()
        }
    }

    override fun pause() {
        controller?.pause()
    }

    override fun release() {
        controller?.release()
        scope.cancel()
        _playerStateFlow.value = PlayerState.DEFAULT
    }

    override fun getCurrentPosition(): Int = controller?.currentPosition?.toInt() ?: 0

    override fun setOnCompletionListener(listener: () -> Unit) {
        controller?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) listener()
            }
        })
    }
}
