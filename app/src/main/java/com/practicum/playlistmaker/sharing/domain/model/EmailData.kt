package com.practicum.playlistmaker.sharing.domain.model

data class EmailData (
    val mail: String,
    val mailTo:String = "mailto:"
    )