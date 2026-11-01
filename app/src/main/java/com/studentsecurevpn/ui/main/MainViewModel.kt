package com.studentsecurevpn.ui.main

import android.app.Application
import android.net.TrafficStats
import android.os.Process
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.studentsecurevpn.data.local.entity.ServerEntity
import com.studentsecurevpn.domain.model.ConnectionStatus
import com.studentsecurevpn.domain.model.DnsProvider
import com.studentsecurevpn.util.Formatters
import com.studentsecurevpn.util.ServiceLocator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Shared Main ViewModel for Home, Server selection, and Settings fragments. */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val serverRepository = ServiceLocator.serverRepository
    private val settingsRepository = ServiceLocator.settingsRepository
    private val vpnRepository = ServiceLocator.vpnRepository

    val servers: LiveData<List<ServerEntity>> = serverRepository.servers.asLiveData()
    val settings = settingsRepository.settings.asLiveData()
    val status: LiveData<ConnectionStatus> = vpnRepository.status
    val error: LiveData<String?> = vpnRepository.error

    private val _selectedServer = MutableLiveData<ServerEntity?>(null)
    val selectedServer: LiveData<ServerEntity?> = _selectedServer
    private val _duration = MutableLiveData("00:00:00")
    val duration: LiveData<String> = _duration
    private val _usage = MutableLiveData("0 B downloaded • 0 B uploaded")
    val usage: LiveData<String> = _usage

    private var timerJob: Job? = null
    private var startedAt = 0L
    private var startRx = 0L
    private var startTx = 0L

    init {
        viewModelScope.launch {
            serverRepository.selectedServerId()?.let { id -> _selectedServer.value = serverRepository.getServer(id) }
            serverRepository.refreshServers()
        }
    }

    fun selectServer(server: ServerEntity) {
        serverRepository.selectServer(server.id)
        _selectedServer.value = server
    }

    fun connect() {
        val server = _selectedServer.value
        if (server == null) return
        viewModelScope.launch {
            val dns = settingsRepository.getDns()
            val result = vpnRepository.connect(server, dns)
            if (result.isSuccess) startStatsTimer()
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            vpnRepository.disconnect()
            stopStatsTimer()
        }
    }

    fun retry() {
        viewModelScope.launch {
            val result = vpnRepository.retry()
            if (result.isSuccess) startStatsTimer()
        }
    }

    fun saveDns(provider: DnsProvider) = viewModelScope.launch { settingsRepository.setDns(provider) }
    fun saveKillSwitch(enabled: Boolean) = viewModelScope.launch { settingsRepository.setKillSwitch(enabled) }
    fun saveAutoConnectWifi(enabled: Boolean) = viewModelScope.launch { settingsRepository.setAutoConnectWifi(enabled) }
    fun saveTheme(modeName: String) = viewModelScope.launch {
        settingsRepository.setThemeMode(com.studentsecurevpn.domain.model.ThemeMode.valueOf(modeName))
    }

    private fun startStatsTimer() {
        startedAt = System.currentTimeMillis()
        startRx = TrafficStats.getUidRxBytes(Process.myUid()).coerceAtLeast(0)
        startTx = TrafficStats.getUidTxBytes(Process.myUid()).coerceAtLeast(0)
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val elapsed = (System.currentTimeMillis() - startedAt) / 1000
                val rx = (TrafficStats.getUidRxBytes(Process.myUid()).coerceAtLeast(0) - startRx).coerceAtLeast(0)
                val tx = (TrafficStats.getUidTxBytes(Process.myUid()).coerceAtLeast(0) - startTx).coerceAtLeast(0)
                _duration.value = Formatters.duration(elapsed)
                _usage.value = "${Formatters.bytes(rx)} downloaded • ${Formatters.bytes(tx)} uploaded"
                delay(1000)
            }
        }
    }

    private fun stopStatsTimer() {
        timerJob?.cancel()
        timerJob = null
        _duration.value = "00:00:00"
        _usage.value = "0 B downloaded • 0 B uploaded"
    }
}
