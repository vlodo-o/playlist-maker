package com.practicum.playlistmaker.model

import java.text.SimpleDateFormat
import java.util.*

data class Track (
    val trackId: String,            // Id трека
    val trackName: String,          // Название композиции
    val artistName: String,         // Имя исполнителя
    val trackTimeMillis: Int,       // Продолжительность трека
    val artworkUrl100: String,      // Ссылка на изображение обложки
    val collectionName: String,     // Альбом
    val releaseDate: String,        // Год релиза трека
    val primaryGenreName: String,   // Жанр трека
    val country: String             // Страна (стора?)
)
{
    val trackTime:String
        get() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

    val releaseYear:String
        get() = releaseDate.substringBefore("-")

    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")


}