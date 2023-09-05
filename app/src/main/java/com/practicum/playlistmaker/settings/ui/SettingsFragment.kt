package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: Toolbar
    private lateinit var themeSwitch: SwitchMaterial
    private lateinit var shareButton: Button
    private lateinit var supportButton: Button
    private lateinit var agreementButton: Button
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        viewModel.themeSettingsState.observe(viewLifecycleOwner) { themeSettings ->
            themeSwitch.isChecked = themeSettings.darkTheme
        }

    }

    private fun initViews() {
        toolbar = binding.toolbar
        themeSwitch = binding.themeSwitch
        shareButton = binding.shareButton
        supportButton = binding.supportButton
        agreementButton = binding.agreementButton
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}