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

import com.bp22intel.edgesentinel.domain.model.CellTower
import com.bp22intel.edgesentinel.domain.repository.CellRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Retrieves current and historical cell tower information from the repository.
 */
class GetCurrentCellInfoUseCase @Inject constructor(
    private val cellRepository: CellRepository
) {

    /** Returns a reactive stream of all observed cell towers. */
    fun observeAll(): Flow<List<CellTower>> = cellRepository.getAllCells()

    /** Returns the cell tower matching the given [cid], or null if not found. */
    suspend fun getByCid(cid: Int): CellTower? = cellRepository.getCellByCid(cid)

    /** Returns all known cells in the given LAC/TAC area. */
    suspend fun getKnownCellsForLac(lacTac: Int): List<CellTower> =
        cellRepository.getKnownCellsForLac(lacTac)
}
