package com.studentsecurevpn.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.studentsecurevpn.data.local.entity.ServerEntity
import com.studentsecurevpn.domain.model.ConnectionStatus
import com.studentsecurevpn.domain.model.DnsProvider
import com.studentsecurevpn.service.VpnMonitorService
import com.studentsecurevpn.service.VpnNotificationHelper
import com.studentsecurevpn.vpn.WireGuardTunnelManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Coordinates VPN connection state, retries, errors, and notifications. */
class VpnRepository(
    private val context: Context,
    private val tunnelManager: WireGuardTunnelManager,
    private val notifications: VpnNotificationHelper
) {
    private val _status = MutableLiveData(ConnectionStatus.DISCONNECTED)
    val status: LiveData<ConnectionStatus> = _status

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private var lastServer: ServerEntity? = null
    private var lastDns: DnsProvider = DnsProvider.CLOUDFLARE

    suspend fun connect(server: ServerEntity, dnsProvider: DnsProvider): Result<Unit> = withContext(Dispatchers.IO) {
        lastServer = server
        lastDns = dnsProvider
        _status.postValue(ConnectionStatus.CONNECTING)
        _error.postValue(null)
        VpnMonitorService.start(context, ConnectionStatus.CONNECTING)
        val result = tunnelManager.connect(server, dnsProvider)
        result.onSuccess {
            _status.postValue(ConnectionStatus.CONNECTED)
            VpnMonitorService.start(context, ConnectionStatus.CONNECTED)
            notifications.showStatus(ConnectionStatus.CONNECTED)
        }.onFailure { throwable ->
            _status.postValue(ConnectionStatus.ERROR)
            _error.postValue(throwable.message ?: "Connection failed")
            VpnMonitorService.start(context, ConnectionStatus.ERROR)
            notifications.showStatus(ConnectionStatus.ERROR)
        }
        result
    }

    suspend fun retry(): Result<Unit> {
        val server = lastServer ?: return Result.failure(IllegalStateException("Select a server before retrying"))
        return connect(server, lastDns)
    }

    suspend fun disconnect() = withContext(Dispatchers.IO) {
        tunnelManager.disconnect()
        _status.postValue(ConnectionStatus.DISCONNECTED)
        _error.postValue(null)
        context.stopService(Intent(context, VpnMonitorService::class.java))
        notifications.showStatus(ConnectionStatus.DISCONNECTED)
    }
}
