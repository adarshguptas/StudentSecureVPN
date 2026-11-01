package com.studentsecurevpn.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Server metadata. wireGuardConfig must come from a legitimate VPN provider/backend.
 * No unauthorized or billing-bypass endpoints are embedded in this project.
 */
@Entity(tableName = "servers")
data class ServerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val country: String,
    val city: String,
    val isPremium: Boolean,
    val latencyMs: Int,
    val loadPercent: Int,
    val wireGuardConfig: String?
)
