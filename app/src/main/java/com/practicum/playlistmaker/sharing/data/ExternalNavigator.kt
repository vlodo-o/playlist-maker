package com.practicum.playlistmaker.sharing.data

import com.practicum.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(shareText:String)
    fun openLink(termsLink:String)
    fun openEmail(supportEmailData: EmailData)
}

