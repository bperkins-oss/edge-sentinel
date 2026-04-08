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
import androidx.room.Query
import com.bp22intel.edgesentinel.data.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Insert
    suspend fun insert(alert: AlertEntity): Long

    @Query("SELECT * FROM alerts ORDER BY timestamp DESC")
    fun getAll(): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE severity = :severity ORDER BY timestamp DESC")
    fun getBySeverity(severity: String): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): AlertEntity?

    @Query("UPDATE alerts SET acknowledged = 1 WHERE id = :id")
    suspend fun acknowledge(id: Long)

    @Query("DELETE FROM alerts WHERE timestamp < :before")
    suspend fun deleteBefore(before: Long)
}
