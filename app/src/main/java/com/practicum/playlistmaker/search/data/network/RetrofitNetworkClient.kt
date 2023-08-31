package com.practicum.playlistmaker.search.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient: NetworkClient {

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(ITunesSearchApi::class.java)

    override suspend fun doRequest(query: String): NetworkResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getTrack(query)
                response.apply { resultCode = 200 }
            } catch (e: Throwable) {
                NetworkResponse().apply { resultCode = -1 }
            }
        }
    }

    companion object {
        const val BASE_URL = "https://itunes.apple.com"
    }

}