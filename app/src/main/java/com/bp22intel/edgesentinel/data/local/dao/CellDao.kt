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
import com.bp22intel.edgesentinel.data.local.entity.CellTowerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CellDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cell: CellTowerEntity): Long

    @Update
    suspend fun update(cell: CellTowerEntity)

    @Query("SELECT * FROM cells ORDER BY last_seen DESC")
    fun getAll(): Flow<List<CellTowerEntity>>

    @Query("SELECT * FROM cells WHERE cid = :cid LIMIT 1")
    suspend fun getByCid(cid: Int): CellTowerEntity?

    @Query("SELECT * FROM cells WHERE lac_tac = :lacTac")
    suspend fun getByLacTac(lacTac: Int): List<CellTowerEntity>
}
