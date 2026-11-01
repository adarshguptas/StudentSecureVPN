package com.studentsecurevpn.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.studentsecurevpn.R
import com.studentsecurevpn.domain.model.ConnectionStatus
import com.studentsecurevpn.ui.main.MainActivity

/** Creates user-visible VPN status notifications required for foreground work. */
class VpnNotificationHelper(private val context: Context) {
    init { createChannel() }

    fun build(status: ConnectionStatus): Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_vpn_shield)
            .setContentTitle("Student Secure VPN")
            .setContentText(statusText(status))
            .setContentIntent(pendingIntent)
            .setOngoing(status == ConnectionStatus.CONNECTED || status == ConnectionStatus.CONNECTING)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    fun showStatus(status: ConnectionStatus) {
        runCatching { NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, build(status)) }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "VPN status", NotificationManager.IMPORTANCE_LOW)
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun statusText(status: ConnectionStatus): String = when (status) {
        ConnectionStatus.CONNECTED -> "VPN connected"
        ConnectionStatus.CONNECTING -> "VPN connecting"
        ConnectionStatus.DISCONNECTED -> "VPN disconnected"
        ConnectionStatus.ERROR -> "VPN connection error"
    }

    companion object {
        const val CHANNEL_ID = "vpn_status"
        const val NOTIFICATION_ID = 1001
    }
}
