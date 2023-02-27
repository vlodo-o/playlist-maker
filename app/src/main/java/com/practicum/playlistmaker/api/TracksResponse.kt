package com.practicum.playlistmaker.api

import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.model.Track

data class TracksResponse(@SerializedName("results") val tracks: ArrayList<Track>)