package com.practicum.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import java.text.SimpleDateFormat
import java.util.*

class PlayTimer(private val interactor: PlayerInteractor, private val setDuration: (ms: String) -> Unit) {

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var runnable = getRunnable()

    fun getRunnable(): Runnable {
        return object: Runnable {
            override fun run() {
                // Вывод позиции воспроизведения в медиаплеере
                setDuration.invoke(millisecondsToString(interactor.getCurrentPosition()))
                mainThreadHandler.postDelayed(this, DURATION_UPDATE_DELAY_MS)
            } }
    }

    fun startTimer() {
        mainThreadHandler.postDelayed(runnable, DURATION_UPDATE_DELAY_MS)
    }


    fun pauseTimer() {
        mainThreadHandler.removeCallbacks(runnable)
    }

    fun millisecondsToString(duration: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(duration)
    }

    companion object {
        private const val DURATION_UPDATE_DELAY_MS = 300L
    }
}