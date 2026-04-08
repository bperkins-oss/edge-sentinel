/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024 BP22 Intel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.bp22intel.edgesentinel.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trusted_networks")
data class TrustedNetworkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "bssid") val bssid: String,        // MAC address of the AP
    @ColumnInfo(name = "ssid") val ssid: String,           // Network name
    @ColumnInfo(name = "label") val label: String? = null,  // User-friendly label ("Kitchen AP", "Guest Network")
    @ColumnInfo(name = "added_at") val addedAt: Long = System.currentTimeMillis()
)
