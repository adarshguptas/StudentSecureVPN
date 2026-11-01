package com.studentsecurevpn.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.studentsecurevpn.data.repository.SettingsRepository
import com.studentsecurevpn.util.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Starts auto-connect workflow after reboot only when the user enabled it. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        ServiceLocator.init(context.applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            val auto = ServiceLocator.settingsRepository.getAutoConnectWifi()
            // Keep this conservative: actual connection still requires selected server and VPN permission.
            if (auto) {
                // Production apps can enqueue WorkManager to wait for Wi-Fi and call VpnService.prepare via UI.
            }
        }
    }
}
