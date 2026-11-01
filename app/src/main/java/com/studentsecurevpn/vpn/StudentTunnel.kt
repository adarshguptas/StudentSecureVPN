package com.studentsecurevpn.vpn

import com.wireguard.android.backend.Tunnel

/** Small WireGuard Tunnel implementation used by GoBackend callbacks. */
class StudentTunnel(
    private val tunnelName: String,
    private val onChange: (Tunnel.State) -> Unit
) : Tunnel {
    override fun getName(): String = tunnelName
    override fun onStateChange(newState: Tunnel.State) = onChange(newState)
}
