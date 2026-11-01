package com.studentsecurevpn.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Simple key/value table for user-controlled app settings. */
@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String
)
