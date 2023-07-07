package com.practicum.playlistmaker.main.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.practicum.playlistmaker.medialib.ui.activity.MedialibActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.ui.view_model.MainViewModel
import com.practicum.playlistmaker.player.ui.models.NavigationState
import com.practicum.playlistmaker.search.ui.activity.SearchActivity
import com.practicum.playlistmaker.settings.ui.activity.SettingsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.navigationLiveData.observe(this) { navigationState ->
            when(navigationState) {
                is NavigationState.SearchScreen -> {
                    val displayIntent = Intent(this, SearchActivity::class.java)
                    startActivity(displayIntent)
                }
                is NavigationState.MedialibScreen -> {
                    val displayIntent = Intent(this, MedialibActivity::class.java)
                    startActivity(displayIntent)
                }
                is NavigationState.SettingsScreen -> {
                    val displayIntent = Intent(this, SettingsActivity::class.java)
                    startActivity(displayIntent)
                }

            }
        }

        val searchButton = findViewById<Button>(R.id.search_button)
        searchButton.setOnClickListener {
            viewModel.openSearchScreen()
        }

        val mediaButton = findViewById<Button>(R.id.media_button)
        mediaButton.setOnClickListener {
            viewModel.openMedialibScreen()
        }

        val settingsButton = findViewById<Button>(R.id.settings_button)
        settingsButton.setOnClickListener {
            viewModel.openSettingsScreen()
        }
    }
}