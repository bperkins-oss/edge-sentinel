/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024 BP22 Intel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.bp22intel.edgesentinel.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bp22intel.edgesentinel.data.local.dao.AlertDao
import com.bp22intel.edgesentinel.data.local.dao.BaselineDao
import com.bp22intel.edgesentinel.data.local.dao.BleDeviceDao
import com.bp22intel.edgesentinel.data.local.dao.CellDao
import com.bp22intel.edgesentinel.data.local.dao.KnownTowerDao
import com.bp22intel.edgesentinel.data.local.dao.ScanDao
import com.bp22intel.edgesentinel.data.local.entity.AlertEntity
import com.bp22intel.edgesentinel.data.local.entity.BaselineEntity
import com.bp22intel.edgesentinel.data.local.entity.BleDeviceEntity
import com.bp22intel.edgesentinel.data.local.entity.CellTowerEntity
import com.bp22intel.edgesentinel.data.local.entity.KnownTowerEntity
import com.bp22intel.edgesentinel.data.local.entity.ScanEntity

@Database(
    entities = [
        CellTowerEntity::class,
        AlertEntity::class,
        ScanEntity::class,
        BleDeviceEntity::class,
        BaselineEntity::class,
        KnownTowerEntity::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class EdgeSentinelDatabase : RoomDatabase() {
    abstract fun cellDao(): CellDao
    abstract fun alertDao(): AlertDao
    abstract fun scanDao(): ScanDao
    abstract fun bleDeviceDao(): BleDeviceDao
    abstract fun baselineDao(): BaselineDao
    abstract fun knownTowerDao(): KnownTowerDao
}
