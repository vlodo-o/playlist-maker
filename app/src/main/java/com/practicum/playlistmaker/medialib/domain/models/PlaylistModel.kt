package com.practicum.playlistmaker.medialib.domain.models

import com.practicum.playlistmaker.search.domain.models.Track

data class PlaylistModel (
    val id: Int,
    val name: String,
    val description: String,
    val imagePath: String,
    val tracks: MutableList<String>,
    var tracksCount: Int
)