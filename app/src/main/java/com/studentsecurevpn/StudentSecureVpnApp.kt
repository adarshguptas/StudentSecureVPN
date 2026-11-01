package com.studentsecurevpn

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.studentsecurevpn.data.local.AppDatabase
import com.studentsecurevpn.data.repository.SettingsRepository
import com.studentsecurevpn.util.ServiceLocator
import kotlinx.coroutines.launch

/**
 * Application entry point. It creates shared singletons and applies the saved theme early.
 */
class StudentSecureVpnApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
        val settingsRepository = SettingsRepository(AppDatabase.getInstance(this).settingsDao())
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            when (settingsRepository.getThemeMode()) {
                "LIGHT" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "DARK" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
}
