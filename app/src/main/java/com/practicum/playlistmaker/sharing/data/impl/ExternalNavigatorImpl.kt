package com.practicum.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl (private val context: Context) : ExternalNavigator {

    val emailSubject = context.getString(R.string.mail_subject)
    val emailText = context.getString(R.string.mail_text)

    override fun shareLink(shareText: String) {
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
            context.startActivity(Intent.createChooser(this, null).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun openLink(termsLink: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(termsLink)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(browserIntent)
    }

    override fun openEmail(supportEmailData: EmailData) {
        Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.mail))
            putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            putExtra(Intent.EXTRA_TEXT, emailText)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(this)
        }
    }
}
