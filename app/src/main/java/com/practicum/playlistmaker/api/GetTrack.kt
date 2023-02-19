package com.practicum.playlistmaker.api

import com.practicum.playlistmaker.model.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val iTunesBaseUrl = "https://itunes.apple.com"

private val retrofit = Retrofit.Builder()
    .baseUrl(iTunesBaseUrl)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private val iTunesSearch = retrofit.create(ITunesSearchApi::class.java)

    fun searchTracks(query:String) : List<Track> {

        val tracks = ArrayList<Track>()

        iTunesSearch.getTrack(query).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(call: Call<TracksResponse>,
                                    response: Response<TracksResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        if (response.body()?.tracks?.isNotEmpty() == true) {
                            tracks.clear()
                            tracks.addAll(response.body()?.tracks!!)
                        }

                    }
                }

            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {

            }

        })
        return tracks
    }

