package com.practicum.playlistmaker.medialib.domain.models

import java.io.Serializable

data class PlaylistModel (
    val id: Int,
    val name: String,
    val description: String,
    val imagePath: String,
    val tracks: MutableList<String>,
    var tracksCount: Int
): Serializable