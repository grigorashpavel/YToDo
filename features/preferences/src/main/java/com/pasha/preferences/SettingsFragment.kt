package com.pasha.preferences

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.pasha.android_core.di.findDependencies
import com.pasha.android_core.preferences.PreferencesManager
import com.pasha.preferences.databinding.FragmentSettingsBinding
import com.pasha.preferences.di.DaggerSettingsComponent
import javax.inject.Inject


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!


    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DaggerSettingsComponent.factory()
            .create(findDependencies())
            .inject(this)

        configureThemeUi()
        setNavigationBackListener()
        setThemeSegmentedButtonListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.btnToggleGroup
        _binding = null
    }

    private fun setThemeSegmentedButtonListener() {
        binding.btnLight.setOnClickListener {
            binding.tvCurrentThemeOption.setText(R.string.theme_type_light)

            preferencesManager.configureTheme(PreferencesManager.Companion.ThemeType.Light)
        }

        binding.btnNight.setOnClickListener {
            binding.tvCurrentThemeOption.setText(R.string.theme_type_night)

            preferencesManager.configureTheme(PreferencesManager.Companion.ThemeType.Night)

        }

        binding.btnSystem.setOnClickListener {
            binding.tvCurrentThemeOption.setText(R.string.theme_type_system)

            preferencesManager.configureTheme(PreferencesManager.Companion.ThemeType.System)
        }
    }

    private fun configureThemeUi() {
        when (preferencesManager.themeType) {
            PreferencesManager.Companion.ThemeType.Light -> {
                binding.tvCurrentThemeOption.setText(R.string.theme_type_light)
                binding.btnLight.isChecked = true
            }

            PreferencesManager.Companion.ThemeType.Night -> {
                binding.tvCurrentThemeOption.setText(R.string.theme_type_night)
                binding.btnNight.isChecked = true
            }

            PreferencesManager.Companion.ThemeType.System -> {
                binding.tvCurrentThemeOption.setText(R.string.theme_type_system)
                binding.btnSystem.isChecked = true
            }
        }
    }

    private fun setNavigationBackListener() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}