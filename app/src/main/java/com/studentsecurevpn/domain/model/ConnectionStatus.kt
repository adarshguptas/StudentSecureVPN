package com.studentsecurevpn.domain.model

/** VPN connection lifecycle states shown in the UI and notification. */
enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}
