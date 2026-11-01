package com.studentsecurevpn.util

import android.content.Context
import com.studentsecurevpn.data.local.AppDatabase
import com.studentsecurevpn.data.remote.ApiClient
import com.studentsecurevpn.data.repository.AuthRepository
import com.studentsecurevpn.data.repository.ServerRepository
import com.studentsecurevpn.data.repository.SettingsRepository
import com.studentsecurevpn.data.repository.VpnRepository
import com.studentsecurevpn.service.VpnNotificationHelper
import com.studentsecurevpn.vpn.WireGuardTunnelManager

/** Lightweight dependency provider to keep the sample simple without a DI framework. */
object ServiceLocator {
    private lateinit var appContext: Context
    lateinit var securePrefs: SecurePreferenceManager private set
    lateinit var authRepository: AuthRepository private set
    lateinit var serverRepository: ServerRepository private set
    lateinit var settingsRepository: SettingsRepository private set
    lateinit var vpnRepository: VpnRepository private set

    fun init(context: Context) {
        appContext = context.applicationContext
        val db = AppDatabase.getInstance(appContext)
        securePrefs = SecurePreferenceManager(appContext)
        val api = ApiClient.create()
        authRepository = AuthRepository(api, securePrefs)
        serverRepository = ServerRepository(api, db.serverDao(), securePrefs)
        settingsRepository = SettingsRepository(db.settingsDao())
        vpnRepository = VpnRepository(
            appContext,
            WireGuardTunnelManager(appContext),
            VpnNotificationHelper(appContext)
        )
    }
}
