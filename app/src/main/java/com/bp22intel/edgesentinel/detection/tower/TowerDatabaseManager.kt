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

package com.bp22intel.edgesentinel.detection.tower

import android.content.Context
import com.bp22intel.edgesentinel.data.local.dao.KnownTowerDao
import com.bp22intel.edgesentinel.data.local.entity.KnownTowerEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TowerDatabaseManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val knownTowerDao: KnownTowerDao
) {
    companion object {
        // OpenCelliD download URL pattern
        // Users need to register for a free API token at opencellid.org
        // Or use the community mirror at https://opencellid.org/downloads.php
        private const val OPENCELLID_BASE = "https://opencellid.org/ocid/downloads"
        
        // MCC to country name mapping for UI
        val MCC_COUNTRIES = mapOf(
            310 to "United States", 311 to "United States", 312 to "United States",
            302 to "Canada",
            234 to "United Kingdom", 235 to "United Kingdom",
            262 to "Germany",
            208 to "France",
            222 to "Italy",
            440 to "Japan", 441 to "Japan",
            450 to "South Korea",
            460 to "China",
            520 to "Thailand",
            510 to "Indonesia",
            515 to "Philippines",
            425 to "Israel",
            420 to "Saudi Arabia",
            424 to "United Arab Emirates",
            286 to "Turkey",
            602 to "Egypt",
            250 to "Russia",
            257 to "Belarus",
            432 to "Iran",
            467 to "North Korea",
            734 to "Venezuela",
            414 to "Myanmar"
        )

        // Countries from travel mode profiles (high priority)
        val PRIORITY_MCCS = listOf(460, 250, 432, 424, 420, 467, 286, 602, 425, 257, 734, 414)
    }

    private val _importProgress = MutableStateFlow<ImportProgress?>(null)
    val importProgress: StateFlow<ImportProgress?> = _importProgress.asStateFlow()

    data class ImportProgress(
        val mcc: Int,
        val countryName: String,
        val totalRows: Int,
        val importedRows: Int,
        val status: ImportStatus
    )

    enum class ImportStatus { DOWNLOADING, PARSING, IMPORTING, COMPLETE, ERROR }

    data class CountryDatabaseInfo(
        val mcc: Int,
        val countryName: String,
        val towerCount: Int,
        val isInstalled: Boolean
    )

    suspend fun getInstalledCountries(): List<CountryDatabaseInfo> {
        val installed = knownTowerDao.getInstalledCountries().toSet()
        return MCC_COUNTRIES.map { (mcc, name) ->
            CountryDatabaseInfo(
                mcc = mcc,
                countryName = name,
                towerCount = if (installed.contains(mcc)) knownTowerDao.getTowerCountByCountry(mcc) else 0,
                isInstalled = installed.contains(mcc)
            )
        }.sortedBy { it.countryName }
    }

    suspend fun getTotalTowerCount(): Int = knownTowerDao.getTowerCount()

    /**
     * Import towers from a CSV file (OpenCelliD format).
     * CSV columns: radio,mcc,mnc,lac,cid,unit,lon,lat,range,samples,changeable,created,updated,averageSignal
     * Users download the CSV from opencellid.org and import via the app.
     */
    suspend fun importFromCsv(inputStream: InputStream, filterMcc: Int? = null) {
        withContext(Dispatchers.IO) {
            val reader = BufferedReader(InputStreamReader(inputStream))
            val batch = mutableListOf<KnownTowerEntity>()
            var lineCount = 0
            var importedCount = 0

            reader.useLines { lines ->
                lines.drop(1).forEach { line -> // skip header
                    lineCount++
                    try {
                        val parts = line.split(",")
                        if (parts.size >= 9) {
                            val radio = parts[0]
                            val mcc = parts[1].toIntOrNull() ?: return@forEach
                            val mnc = parts[2].toIntOrNull() ?: return@forEach
                            val lac = parts[3].toIntOrNull() ?: return@forEach
                            val cid = parts[4].toIntOrNull() ?: return@forEach
                            val lon = parts[6].toDoubleOrNull() ?: return@forEach
                            val lat = parts[7].toDoubleOrNull() ?: return@forEach
                            val range = parts[8].toIntOrNull() ?: 1000

                            if (filterMcc != null && mcc != filterMcc) return@forEach

                            batch.add(KnownTowerEntity(
                                mcc = mcc, mnc = mnc, lac = lac, cid = cid,
                                latitude = lat, longitude = lon,
                                range = range, radio = radio
                            ))

                            if (batch.size >= 500) {
                                knownTowerDao.insertTowers(batch.toList())
                                importedCount += batch.size
                                batch.clear()
                                _importProgress.value = _importProgress.value?.copy(
                                    importedRows = importedCount
                                )
                            }
                        }
                    } catch (_: Exception) { /* skip malformed lines */ }
                }
            }

            // Insert remaining
            if (batch.isNotEmpty()) {
                knownTowerDao.insertTowers(batch.toList())
                importedCount += batch.size
            }

            _importProgress.value = _importProgress.value?.copy(
                importedRows = importedCount,
                status = ImportStatus.COMPLETE
            )
        }
    }

    suspend fun deleteCountry(mcc: Int) {
        knownTowerDao.deleteTowersByCountry(mcc)
    }
}