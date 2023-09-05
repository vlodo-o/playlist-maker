package com.practicum.playlistmaker.medialib.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.databinding.FragmentMedialibBinding

class MedialibFragment : Fragment()  {

    private var _binding: FragmentMedialibBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: Toolbar
    private lateinit var tabMediator: TabLayoutMediator
    private lateinit var medialibTabLayout: TabLayout
    private lateinit var medialibViewPager: ViewPager2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMedialibBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        medialibViewPager.adapter = MedialibPagerAdapter(childFragmentManager, lifecycle)
        tabMediator = TabLayoutMediator(medialibTabLayout, medialibViewPager) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.favorite_tracks)
                1 -> tab.text = getString(R.string.playlist)
            }
        }
        tabMediator.attach()
    }

    private fun initViews() {
        toolbar = binding.toolbar
        medialibViewPager = binding.medialibViewPager
        medialibTabLayout = binding.tabLayout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MedialibFragment()
    }

}