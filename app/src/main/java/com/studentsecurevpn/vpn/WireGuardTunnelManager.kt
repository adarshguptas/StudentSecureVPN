package com.studentsecurevpn.vpn

import android.content.Context
import com.studentsecurevpn.data.local.entity.ServerEntity
import com.studentsecurevpn.domain.model.DnsProvider
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.StringReader

/**
 * WireGuard protocol adapter. It uses the official WireGuard Android backend and
 * accepts only explicit WireGuard configs supplied by an authorized backend/admin.
 */
class WireGuardTunnelManager(context: Context) {
    private val appContext = context.applicationContext
    private val backend by lazy { GoBackend(appContext) }
    private var currentTunnel: StudentTunnel? = null

    suspend fun connect(server: ServerEntity, dnsProvider: DnsProvider): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val rawConfig = server.wireGuardConfig
            require(!rawConfig.isNullOrBlank()) {
                "This sample server has no WireGuard profile. Import authorized server configs from your VPN backend."
            }
            validateConfig(rawConfig)
            val configWithDns = applyDns(rawConfig, dnsProvider)
            val config = Config.parse(BufferedReader(StringReader(configWithDns)))
            val tunnel = StudentTunnel(server.id) { }
            backend.setState(tunnel, Tunnel.State.UP, config)
            currentTunnel = tunnel
        }
    }

    suspend fun disconnect() = withContext(Dispatchers.IO) {
        currentTunnel?.let { tunnel ->
            runCatching { backend.setState(tunnel, Tunnel.State.DOWN, null) }
        }
        currentTunnel = null
    }

    private fun validateConfig(config: String) {
        require(config.contains("[Interface]")) { "WireGuard config is missing [Interface]" }
        require(config.contains("[Peer]")) { "WireGuard config is missing [Peer]" }
        require(!config.contains("0.0.0.0:0")) { "WireGuard endpoint is invalid" }
    }

    /** Rewrites or inserts DNS inside [Interface] without touching keys or endpoints. */
    private fun applyDns(config: String, provider: DnsProvider): String {
        val dnsLine = "DNS = ${provider.addresses.joinToString(", ")}"
        val output = mutableListOf<String>()
        var inInterface = false
        var dnsWritten = false
        config.lineSequence().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.equals("[Interface]", ignoreCase = true)) {
                inInterface = true
                output += line
                return@forEach
            }
            if (inInterface && trimmed.startsWith("[") && !trimmed.equals("[Interface]", ignoreCase = true)) {
                if (!dnsWritten) {
                    output += dnsLine
                    dnsWritten = true
                }
                inInterface = false
            }
            if (inInterface && trimmed.startsWith("DNS", ignoreCase = true)) {
                if (!dnsWritten) {
                    output += dnsLine
                    dnsWritten = true
                }
            } else {
                output += line
            }
        }
        if (inInterface && !dnsWritten) output += dnsLine
        return output.joinToString("\n")
    }
}
