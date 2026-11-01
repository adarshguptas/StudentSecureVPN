package com.studentsecurevpn.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.studentsecurevpn.R
import com.studentsecurevpn.data.repository.SettingsRepository
import com.studentsecurevpn.databinding.FragmentSettingsBinding
import com.studentsecurevpn.domain.model.DnsProvider
import com.studentsecurevpn.ui.about.AboutActivity
import com.studentsecurevpn.ui.main.MainViewModel

/** Settings screen for DNS, kill switch preference, Wi-Fi auto connect, and theme. */
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.cloudflareRadio.isChecked = true
        binding.dnsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val provider = when (checkedId) {
                R.id.googleRadio -> DnsProvider.GOOGLE
                R.id.quad9Radio -> DnsProvider.QUAD9
                else -> DnsProvider.CLOUDFLARE
            }
            viewModel.saveDns(provider)
        }
        binding.killSwitch.setOnCheckedChangeListener { _, checked -> viewModel.saveKillSwitch(checked) }
        binding.autoConnectWifi.setOnCheckedChangeListener { _, checked -> viewModel.saveAutoConnectWifi(checked) }
        binding.themeToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            when (checkedId) {
                R.id.lightThemeButton -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    viewModel.saveTheme("LIGHT")
                }
                R.id.darkThemeButton -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    viewModel.saveTheme("DARK")
                }
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    viewModel.saveTheme("SYSTEM")
                }
            }
        }
        binding.systemThemeButton.isChecked = true
        binding.aboutButton.setOnClickListener { startActivity(Intent(requireContext(), AboutActivity::class.java)) }

        viewModel.settings.observe(viewLifecycleOwner) { settings ->
            settings.associate { it.key to it.value }.let { map ->
                binding.killSwitch.isChecked = map[SettingsRepository.KEY_KILL_SWITCH]?.toBooleanStrictOrNull() ?: false
                binding.autoConnectWifi.isChecked = map[SettingsRepository.KEY_AUTO_WIFI]?.toBooleanStrictOrNull() ?: false
                when (map[SettingsRepository.KEY_DNS]) {
                    DnsProvider.GOOGLE.name -> binding.googleRadio.isChecked = true
                    DnsProvider.QUAD9.name -> binding.quad9Radio.isChecked = true
                    else -> binding.cloudflareRadio.isChecked = true
                }
                when (map[SettingsRepository.KEY_THEME]) {
                    "LIGHT" -> binding.lightThemeButton.isChecked = true
                    "DARK" -> binding.darkThemeButton.isChecked = true
                    else -> binding.systemThemeButton.isChecked = true
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
