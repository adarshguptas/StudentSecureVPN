package com.studentsecurevpn.data.repository

import com.studentsecurevpn.data.local.dao.ServerDao
import com.studentsecurevpn.data.local.entity.ServerEntity
import com.studentsecurevpn.data.remote.VpnApiService
import com.studentsecurevpn.util.SecurePreferenceManager
import kotlinx.coroutines.flow.Flow

/** Provides server categories from Room and refreshes authorized profiles from the API. */
class ServerRepository(
    private val api: VpnApiService,
    private val serverDao: ServerDao,
    private val securePrefs: SecurePreferenceManager
) {
    val servers: Flow<List<ServerEntity>> = serverDao.observeServers()

    suspend fun refreshServers(): Result<Unit> = runCatching {
        val bearer = securePrefs.token()?.let { "Bearer $it" }
        val remote = api.servers(bearer).map {
            ServerEntity(it.id, it.name, it.country, it.city, it.premium, it.latencyMs, it.loadPercent, it.wireGuardConfig)
        }
        if (remote.isNotEmpty()) serverDao.upsertAll(remote)
    }

    suspend fun getServer(id: String): ServerEntity? = serverDao.getById(id)

    fun selectServer(id: String) = securePrefs.setSelectedServerId(id)
    fun selectedServerId(): String? = securePrefs.selectedServerId()
}
