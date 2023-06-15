package com.practicum.playlistmaker.settings.ui.activity

import com.practicum.playlistmaker.R
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var themeSwitch: SwitchMaterial
    private lateinit var shareButton: Button
    private lateinit var supportButton: Button
    private lateinit var agreementButton: Button
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProvider(this, SettingsViewModel.getViewModelFactory(this))[SettingsViewModel::class.java]

        initViews()
        initListeners()

        setSupportActionBar(toolbar)


        viewModel.themeSettingsState.observe(this) { themeSettings ->
            themeSwitch.isChecked = themeSettings.darkTheme
        }

    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        themeSwitch = findViewById(R.id.theme_switch)
        shareButton = findViewById(R.id.share_button)
        supportButton = findViewById(R.id.support_button)
        agreementButton = findViewById(R.id.agreement_button)
    }

    private fun initListeners() {
        themeSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }
        shareButton.setOnClickListener {
            viewModel.shareApp()
        }
        supportButton.setOnClickListener {
            viewModel.supportEmail()
        }
        agreementButton.setOnClickListener {
            viewModel.openAgreement()
        }
    }
}