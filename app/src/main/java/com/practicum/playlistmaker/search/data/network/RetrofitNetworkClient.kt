package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.NetworkError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient: NetworkClient {

    val api = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(ITunesSearchApi::class.java)

    override fun doRequest(query: String, onSuccess: (List<Track>) -> Unit, onError: (NetworkError) -> Unit) {
        api.getTrack(query).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(
                call: Call<TracksResponse>,
                response: retrofit2.Response<TracksResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            onSuccess.invoke(response.body()?.results!!)
                        } else {
                            onError.invoke(NetworkError.EMPTY_RESULT)
                        }
                    }
                    else ->
                        onError.invoke(NetworkError.CONNECTION_ERROR)
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                onError.invoke(NetworkError.CONNECTION_ERROR)
            }
        })
    }

}