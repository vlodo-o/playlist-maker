package com.practicum.playlistmaker.search

import com.practicum.playlistmaker.search.TracksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesSearchApi {
    @GET("/search?entity=song")
    fun getTrack(@Query("term") text: String): Call<TracksResponse>
}