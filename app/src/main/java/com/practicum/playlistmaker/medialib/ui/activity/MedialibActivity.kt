package com.practicum.playlistmaker.medialib.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.medialib.ui.MedialibPagerAdapter

class MedialibActivity : AppCompatActivity() {

    private lateinit var tabMediator: TabLayoutMediator
    private lateinit var medialibTabLayout: TabLayout
    private lateinit var medialibViewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medialib)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        medialibViewPager = findViewById(R.id.medialib_view_pager)
        medialibTabLayout = findViewById(R.id.tab_layout)

        medialibViewPager.adapter = MedialibPagerAdapter(supportFragmentManager, lifecycle)
        tabMediator = TabLayoutMediator(medialibTabLayout, medialibViewPager) { tab, position ->
            when(position) {
                0 -> tab.text = "Избранные треки"
                1 -> tab.text = "Плейлисты"
            }
        }
        tabMediator.attach()
    }
}