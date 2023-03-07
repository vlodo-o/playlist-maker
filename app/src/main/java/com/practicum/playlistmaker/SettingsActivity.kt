package com.practicum.playlistmaker

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var themeSwitch: SwitchMaterial
    private lateinit var shareButton: Button
    private lateinit var supportButton: Button
    private lateinit var agreementButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val application = application as App
        themeSwitch = findViewById(R.id.theme_switch)
        themeSwitch.isChecked = application.darkTheme

        themeSwitch.setOnCheckedChangeListener { switcher, checked ->
            application.switchTheme(checked)
        }

        shareButton = findViewById(R.id.share_button)
        shareButton.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.shared_url))
                putExtra(Intent.EXTRA_TITLE, getString(R.string.shared_title))
                type = "text/plain"
                startActivity(Intent.createChooser(this, null))
            }
        }

        supportButton = findViewById(R.id.support_button)
        supportButton.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text))
                startActivity(this)
            }
        }

        agreementButton = findViewById(R.id.agreement_button)
        agreementButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.agreement_url)))
            startActivity(browserIntent)
        }
    }
}