package com.practicum.playlistmaker.medialib.domain.models

import java.io.Serializable

data class PlaylistModel (
    val id: Int,
    var name: String,
    var description: String,
    var imagePath: String,
    val tracks: MutableList<String>,
    var tracksCount: Int
): Serializable