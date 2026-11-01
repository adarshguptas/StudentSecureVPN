package com.studentsecurevpn.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.studentsecurevpn.domain.model.ConnectionStatus

/** Foreground service that keeps a transparent VPN status notification visible. */
class VpnMonitorService : Service() {
    private lateinit var notifications: VpnNotificationHelper

    override fun onCreate() {
        super.onCreate()
        notifications = VpnNotificationHelper(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val status = intent?.getStringExtra(EXTRA_STATUS)?.let { runCatching { ConnectionStatus.valueOf(it) }.getOrNull() }
            ?: ConnectionStatus.CONNECTING
        startForeground(VpnNotificationHelper.NOTIFICATION_ID, notifications.build(status))
        if (status == ConnectionStatus.DISCONNECTED || status == ConnectionStatus.ERROR) stopSelf()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val EXTRA_STATUS = "extra_status"
        fun start(context: Context, status: ConnectionStatus) {
            val intent = Intent(context, VpnMonitorService::class.java).putExtra(EXTRA_STATUS, status.name)
            ContextCompat.startForegroundService(context, intent)
        }
    }
}
