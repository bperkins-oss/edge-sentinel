/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024-2026 BP22 Intel. All Rights Reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use of this software, in whole or in
 * part, is strictly prohibited without prior written permission from
 * BP22 Intel.
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
