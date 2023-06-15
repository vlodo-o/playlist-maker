package com.practicum.playlistmaker.search.data.network

import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.search.domain.models.Track

data class TracksResponse(@SerializedName("results") val results: ArrayList<Track>)