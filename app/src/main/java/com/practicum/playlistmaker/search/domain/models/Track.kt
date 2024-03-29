package com.practicum.playlistmaker.search.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Track (
    val trackId: String,            // Id трека
    val trackName: String,          // Название композиции
    val artistName: String,         // Имя исполнителя
    val trackTimeMillis: Int,       // Продолжительность трека
    val artworkUrl100: String,      // Ссылка на изображение обложки
    val artworkUrl60: String,
    val collectionName: String,     // Альбом
    val releaseDate: String,        // Год релиза трека
    val primaryGenreName: String,   // Жанр трека
    val country: String,            // Страна (стора?)
    val previewUrl: String
) : Parcelable
{
    val trackTime:String
        get() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

    val releaseYear:String
        get() = releaseDate.substringBefore("-")

    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

}