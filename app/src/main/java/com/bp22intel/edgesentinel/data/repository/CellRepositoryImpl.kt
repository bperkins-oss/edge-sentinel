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

import com.bp22intel.edgesentinel.data.local.dao.CellDao
import com.bp22intel.edgesentinel.data.local.toDomain
import com.bp22intel.edgesentinel.data.local.toEntity
import com.bp22intel.edgesentinel.domain.model.CellTower
import com.bp22intel.edgesentinel.domain.repository.CellRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CellRepositoryImpl @Inject constructor(
    private val cellDao: CellDao
) : CellRepository {

    override suspend fun insertOrUpdateCell(cell: CellTower) {
        cellDao.insert(cell.toEntity())
    }

    override fun getAllCells(): Flow<List<CellTower>> =
        cellDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getCellByCid(cid: Int): CellTower? =
        cellDao.getByCid(cid)?.toDomain()

    override suspend fun getKnownCellsForLac(lacTac: Int): List<CellTower> =
        cellDao.getByLacTac(lacTac).map { it.toDomain() }
}
