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

package com.bp22intel.edgesentinel.baseline

/**
 * RF fingerprint baseline for a geographic location cluster.
 *
 * Built entirely from the user's own passive observations — no cloud data,
 * no external baseline sharing. Location clustering uses on-device GPS.
 */
data class LocationBaseline(
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Double,
    val label: String? = null,
    val expectedTowers: List<ExpectedTower>,
    val expectedWifiAps: List<ExpectedWifiAp>,
    val bleCountRange: IntRange,
    val networkTypeDistribution: Map<String, Double>,
    val dayProfile: TimeProfile?,
    val nightProfile: TimeProfile?,
    val observationCount: Int,
    val confidence: BaselineConfidence,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Expected cell tower at a baselined location.
 */
data class ExpectedTower(
    val cid: Int,
    val lacTac: Int,
    val mcc: Int,
    val mnc: Int,
    val networkType: String,
    val signalMin: Int,
    val signalMax: Int,
    val signalAvg: Double,
    val appearanceRate: Double
)

/**
 * Expected WiFi access point at a baselined location.
 */
data class ExpectedWifiAp(
    val bssid: String,
    val ssid: String,
    val signalMin: Int,
    val signalMax: Int,
    val signalAvg: Double,
    val appearanceRate: Double
)

/**
 * Time-of-day RF profile (separate day/night to handle normal rush-hour variation).
 */
data class TimeProfile(
    val towers: List<ExpectedTower>,
    val networkTypeDistribution: Map<String, Double>,
    val avgBleCount: Double
)

/**
 * Confidence level based on number of observations.
 */
enum class BaselineConfidence(val minObservations: Int) {
    LEARNING(0),
    LOW(5),
    MEDIUM(10),
    CONFIDENT(20);

    companion object {
        fun fromObservationCount(count: Int): BaselineConfidence = when {
            count >= CONFIDENT.minObservations -> CONFIDENT
            count >= MEDIUM.minObservations -> MEDIUM
            count >= LOW.minObservations -> LOW
            else -> LEARNING
        }
    }
}

/**
 * Result of comparing current RF environment against a stored baseline.
 */
data class BaselineAnomalyResult(
    val locationId: Long,
    val compositeScore: Double,
    val missingTowerScore: Double,
    val unknownTowerScore: Double,
    val signalDeviationScore: Double,
    val networkTypeScore: Double,
    val wifiChangeScore: Double,
    val isNewLocation: Boolean,
    val confidence: BaselineConfidence,
    val details: Map<String, String>
)
