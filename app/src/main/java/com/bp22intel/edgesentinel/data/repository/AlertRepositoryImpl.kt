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

package com.bp22intel.edgesentinel.data.repository

import com.bp22intel.edgesentinel.data.local.dao.AlertDao
import com.bp22intel.edgesentinel.data.local.toDomain
import com.bp22intel.edgesentinel.data.local.toEntity
import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import com.bp22intel.edgesentinel.domain.repository.AlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertRepositoryImpl @Inject constructor(
    private val alertDao: AlertDao
) : AlertRepository {

    override suspend fun insertAlert(alert: Alert): Long =
        alertDao.insert(alert.toEntity())

    override fun getAllAlerts(): Flow<List<Alert>> =
        alertDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override fun getRecentAlerts(limit: Int): Flow<List<Alert>> =
        alertDao.getRecent(limit).map { entities -> entities.map { it.toDomain() } }

    override fun getAlertsByThreatLevel(level: ThreatLevel): Flow<List<Alert>> =
        alertDao.getBySeverity(level.name).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getAlertById(id: Long): Alert? =
        alertDao.getById(id)?.toDomain()

    override suspend fun acknowledgeAlert(id: Long) =
        alertDao.acknowledge(id)

    override suspend fun deleteOldAlerts(beforeTimestamp: Long) =
        alertDao.deleteBefore(beforeTimestamp)
}
