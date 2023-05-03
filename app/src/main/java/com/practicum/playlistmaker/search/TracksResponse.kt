package com.practicum.playlistmaker.search

import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.player.domain.models.Track

data class TracksResponse(@SerializedName("results") val tracks: ArrayList<Track>)