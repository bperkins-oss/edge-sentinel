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

package com.bp22intel.edgesentinel.domain.repository

import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import kotlinx.coroutines.flow.Flow

interface AlertRepository {
    suspend fun insertAlert(alert: Alert): Long
    fun getAllAlerts(): Flow<List<Alert>>
    fun getRecentAlerts(limit: Int): Flow<List<Alert>>
    fun getAlertsByThreatLevel(level: ThreatLevel): Flow<List<Alert>>
    suspend fun getAlertById(id: Long): Alert?
    suspend fun acknowledgeAlert(id: Long)
    suspend fun deleteOldAlerts(beforeTimestamp: Long)
}
