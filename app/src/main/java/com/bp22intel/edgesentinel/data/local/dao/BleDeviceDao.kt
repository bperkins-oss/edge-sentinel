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

package com.bp22intel.edgesentinel.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bp22intel.edgesentinel.data.local.entity.BleDeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BleDeviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: BleDeviceEntity): Long

    @Update
    suspend fun update(device: BleDeviceEntity)

    @Query("SELECT * FROM ble_devices ORDER BY last_seen DESC")
    fun getAll(): Flow<List<BleDeviceEntity>>

    @Query("SELECT * FROM ble_devices WHERE mac_address = :macAddress LIMIT 1")
    suspend fun getByMacAddress(macAddress: String): BleDeviceEntity?

    @Query("SELECT * FROM ble_devices WHERE advertising_data_hash = :hash LIMIT 1")
    suspend fun getByAdvertisingHash(hash: String): BleDeviceEntity?

    @Query("SELECT * FROM ble_devices WHERE is_tracker_type = 1 ORDER BY last_seen DESC")
    fun getTrackers(): Flow<List<BleDeviceEntity>>

    @Query("SELECT * FROM ble_devices WHERE last_seen > :since ORDER BY last_seen DESC")
    fun getRecentDevices(since: Long): Flow<List<BleDeviceEntity>>

    @Query("SELECT * FROM ble_devices WHERE seen_count >= :minCount ORDER BY seen_count DESC")
    fun getFrequentDevices(minCount: Int): Flow<List<BleDeviceEntity>>

    @Query("SELECT COUNT(*) FROM ble_devices WHERE first_seen > :since")
    suspend fun countNewDevicesSince(since: Long): Int

    @Query("DELETE FROM ble_devices WHERE last_seen < :before")
    suspend fun deleteBefore(before: Long)
}
