package com.studentsecurevpn.data.repository

import com.studentsecurevpn.data.local.dao.SettingsDao
import com.studentsecurevpn.data.local.entity.SettingEntity
import com.studentsecurevpn.domain.model.DnsProvider
import com.studentsecurevpn.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

/** Persists non-sensitive settings in Room. */
class SettingsRepository(private val dao: SettingsDao) {
    val settings: Flow<List<SettingEntity>> = dao.observeSettings()

    suspend fun setDns(provider: DnsProvider) = dao.put(SettingEntity(KEY_DNS, provider.name))
    suspend fun getDns(): DnsProvider = DnsProvider.fromName(dao.getValue(KEY_DNS))

    suspend fun setKillSwitch(enabled: Boolean) = dao.put(SettingEntity(KEY_KILL_SWITCH, enabled.toString()))
    suspend fun getKillSwitch(): Boolean = dao.getValue(KEY_KILL_SWITCH)?.toBooleanStrictOrNull() ?: false

    suspend fun setAutoConnectWifi(enabled: Boolean) = dao.put(SettingEntity(KEY_AUTO_WIFI, enabled.toString()))
    suspend fun getAutoConnectWifi(): Boolean = dao.getValue(KEY_AUTO_WIFI)?.toBooleanStrictOrNull() ?: false

    suspend fun setThemeMode(mode: ThemeMode) = dao.put(SettingEntity(KEY_THEME, mode.name))
    suspend fun getThemeMode(): String = dao.getValue(KEY_THEME) ?: ThemeMode.SYSTEM.name

    companion object {
        const val KEY_DNS = "dns_provider"
        const val KEY_KILL_SWITCH = "kill_switch"
        const val KEY_AUTO_WIFI = "auto_connect_wifi"
        const val KEY_THEME = "theme_mode"
    }
}
