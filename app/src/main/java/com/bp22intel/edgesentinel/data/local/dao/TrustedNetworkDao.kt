/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024 BP22 Intel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.bp22intel.edgesentinel.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bp22intel.edgesentinel.data.local.entity.TrustedNetworkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrustedNetworkDao {
    @Query("SELECT * FROM trusted_networks ORDER BY ssid")
    fun getAllTrusted(): Flow<List<TrustedNetworkEntity>>

    @Query("SELECT * FROM trusted_networks WHERE bssid = :bssid LIMIT 1")
    suspend fun findByBssid(bssid: String): TrustedNetworkEntity?

    @Query("SELECT bssid FROM trusted_networks")
    suspend fun getAllTrustedBssids(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(network: TrustedNetworkEntity)

    @Query("DELETE FROM trusted_networks WHERE bssid = :bssid")
    suspend fun removeByBssid(bssid: String)

    @Query("DELETE FROM trusted_networks WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("SELECT COUNT(*) FROM trusted_networks")
    suspend fun getCount(): Int
}
