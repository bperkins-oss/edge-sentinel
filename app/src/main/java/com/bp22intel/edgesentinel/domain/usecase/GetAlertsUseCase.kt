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

package com.bp22intel.edgesentinel.domain.usecase

import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import com.bp22intel.edgesentinel.domain.repository.AlertRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Provides filtered access to alerts from the repository.
 */
class GetAlertsUseCase @Inject constructor(
    private val alertRepository: AlertRepository
) {

    /** Returns all alerts ordered by recency. */
    fun all(): Flow<List<Alert>> = alertRepository.getAllAlerts()

    /** Returns the most recent [limit] alerts. */
    fun recent(limit: Int = 50): Flow<List<Alert>> = alertRepository.getRecentAlerts(limit)

    /** Returns alerts filtered by [level]. */
    fun byThreatLevel(level: ThreatLevel): Flow<List<Alert>> =
        alertRepository.getAlertsByThreatLevel(level)

    /** Retrieves a single alert by its [id]. */
    suspend fun byId(id: Long): Alert? = alertRepository.getAlertById(id)

    /** Marks an alert as acknowledged. */
    suspend fun acknowledge(id: Long) = alertRepository.acknowledgeAlert(id)
}
