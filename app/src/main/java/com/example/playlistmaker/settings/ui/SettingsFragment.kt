package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.App
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.themeSwitcher.isChecked = settingsViewModel.getThemeSettings()

        settingsViewModel.getThemeSettingsLiveData().observe(viewLifecycleOwner) {
            (requireContext() as App).switchTheme(it)
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.switchTheme(checked)
        }

        binding.backButton.setOnClickListener {
            TODO("А может оно и не надо))")
        }

        binding.shareButton.setOnClickListener {
            settingsViewModel.shareApp()
        }

        binding.mailToSupButton.setOnClickListener {
            settingsViewModel.sendMail()
        }

        binding.agreementButton.setOnClickListener {
            settingsViewModel.openTherms()
        }
    }

}