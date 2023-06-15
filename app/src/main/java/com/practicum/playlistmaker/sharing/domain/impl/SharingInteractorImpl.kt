package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    companion object {
        const val APP_LINK = "https://practicum.yandex.ru/android-developer"
        const val SUPPORT_EMAIL = "vabramochkina@yandex.ru"
        const val TERM_LINK = "https://yandex.ru/legal/practicum_offer"
    }

    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return APP_LINK
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(mail = SUPPORT_EMAIL)
    }

    private fun getTermsLink(): String {
        return TERM_LINK
    }
}