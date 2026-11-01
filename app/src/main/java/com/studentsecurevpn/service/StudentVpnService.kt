package com.studentsecurevpn.service

import android.net.VpnService
import com.studentsecurevpn.util.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * App VpnService declaration. Android calls onRevoke when the user/system revokes VPN permission.
 * The WireGuard backend service performs the encrypted tunnel; this class owns app lifecycle cleanup.
 */
class StudentVpnService : VpnService() {
    override fun onRevoke() {
        super.onRevoke()
        CoroutineScope(Dispatchers.IO).launch { ServiceLocator.vpnRepository.disconnect() }
    }
}
