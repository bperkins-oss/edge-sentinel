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
import com.bp22intel.edgesentinel.data.local.entity.KnownTowerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KnownTowerDao {
    @Query("SELECT * FROM known_towers WHERE mcc = :mcc AND mnc = :mnc AND lac = :lac AND cid = :cid LIMIT 1")
    suspend fun findTower(mcc: Int, mnc: Int, lac: Int, cid: Int): KnownTowerEntity?

    @Query("SELECT * FROM known_towers WHERE mcc = :mcc AND mnc = :mnc AND lac = :lac AND cid = :cid")
    fun observeTower(mcc: Int, mnc: Int, lac: Int, cid: Int): Flow<KnownTowerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTowers(towers: List<KnownTowerEntity>)

    @Query("SELECT COUNT(*) FROM known_towers")
    suspend fun getTowerCount(): Int

    @Query("SELECT COUNT(*) FROM known_towers WHERE mcc = :mcc")
    suspend fun getTowerCountByCountry(mcc: Int): Int

    @Query("DELETE FROM known_towers WHERE mcc = :mcc")
    suspend fun deleteTowersByCountry(mcc: Int)

    @Query("SELECT DISTINCT mcc FROM known_towers ORDER BY mcc")
    suspend fun getInstalledCountries(): List<Int>

    @Query("SELECT * FROM known_towers WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng")
    suspend fun findTowersInArea(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): List<KnownTowerEntity>
}