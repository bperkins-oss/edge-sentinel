/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024 BP22 Intel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.bp22intel.edgesentinel.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cells")
data class CellTowerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cid: Int,
    @ColumnInfo(name = "lac_tac")
    val lacTac: Int,
    val mcc: Int,
    val mnc: Int,
    @ColumnInfo(name = "signal_strength")
    val signalStrength: Int,
    @ColumnInfo(name = "network_type")
    val networkType: String,
    val latitude: Double?,
    val longitude: Double?,
    @ColumnInfo(name = "first_seen")
    val firstSeen: Long,
    @ColumnInfo(name = "last_seen")
    val lastSeen: Long,
    @ColumnInfo(name = "times_seen")
    val timesSeen: Int
)
