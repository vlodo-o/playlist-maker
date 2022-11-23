package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search)
        val searchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку поиска!", Toast.LENGTH_SHORT).show()
            }
        }
        searchButton.setOnClickListener(searchClickListener)

        val mediaButton = findViewById<Button>(R.id.media)
        mediaButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку медиатеки!", Toast.LENGTH_SHORT).show()
        }

        val settingsButton = findViewById<Button>(R.id.settings)
        settingsButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку настроек!", Toast.LENGTH_SHORT).show()
        }
    }
}