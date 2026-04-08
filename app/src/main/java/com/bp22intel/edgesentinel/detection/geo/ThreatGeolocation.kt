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

package com.bp22intel.edgesentinel.detection.geo

import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.domain.model.SensorCategory
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import dagger.hilt.android.scopes.ServiceScoped
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Advanced propagation models for cellular, WiFi, and BLE distance estimation
 */
object PropagationModels {
    /**
     * Okumura-Hata model for urban macro-cell (150-1500 MHz)
     * Most accurate for cell tower distance estimation
     * Returns distance in meters
     */
    fun okumuraHataDistance(rssiDbm: Int, frequencyMhz: Double = 900.0, 
                            baseHeightM: Double = 30.0, mobileHeightM: Double = 1.5,
                            environment: Environment = Environment.URBAN): Double {
        // Path loss = RSSI_tx - RSSI_rx (typical eNodeB tx power ~43 dBm for LTE)
        val txPower = 43.0
        val pathLoss = txPower - rssiDbm
        
        // Correction factor for mobile antenna height
        val aHm = when (environment) {
            Environment.URBAN_LARGE -> 3.2 * (log10(11.75 * mobileHeightM)).pow(2) - 4.97
            else -> (1.1 * log10(frequencyMhz) - 0.7) * mobileHeightM - 
                    (1.56 * log10(frequencyMhz) - 0.8)
        }
        
        // Base Okumura-Hata formula (solve for distance)
        // PL = 69.55 + 26.16*log(f) - 13.82*log(hb) - a(hm) + (44.9 - 6.55*log(hb))*log(d)
        val A = 69.55 + 26.16 * log10(frequencyMhz) - 13.82 * log10(baseHeightM) - aHm
        val B = 44.9 - 6.55 * log10(baseHeightM)
        
        // Suburban/rural corrections
        val correctedPL = when (environment) {
            Environment.SUBURBAN -> pathLoss + 2 * (log10(frequencyMhz / 28)).pow(2) + 5.4
            Environment.RURAL -> pathLoss + 4.78 * (log10(frequencyMhz)).pow(2) - 
                                18.33 * log10(frequencyMhz) + 40.94
            else -> pathLoss.toDouble()
        }
        
        // Solve for d: correctedPL = A + B*log10(d) → d = 10^((correctedPL - A) / B)
        val logD = (correctedPL - A) / B
        val distanceKm = 10.0.pow(logD)
        return (distanceKm * 1000).coerceIn(10.0, 50000.0) // 10m to 50km bounds
    }
    
    /**
     * COST 231-Hata for higher frequencies (1500-2000 MHz, good for LTE bands)
     */
    fun cost231Distance(rssiDbm: Int, frequencyMhz: Double = 1800.0,
                        baseHeightM: Double = 30.0, mobileHeightM: Double = 1.5,
                        isUrban: Boolean = true): Double {
        val txPower = 43.0
        val pathLoss = txPower - rssiDbm
        
        val aHm = (1.1 * log10(frequencyMhz) - 0.7) * mobileHeightM - 
                  (1.56 * log10(frequencyMhz) - 0.8)
        val cM = if (isUrban) 3.0 else 0.0
        
        // PL = 46.3 + 33.9*log(f) - 13.82*log(hb) - a(hm) + (44.9-6.55*log(hb))*log(d) + cM
        val A = 46.3 + 33.9 * log10(frequencyMhz) - 13.82 * log10(baseHeightM) - aHm + cM
        val B = 44.9 - 6.55 * log10(baseHeightM)
        
        val logD = (pathLoss - A) / B
        val distanceKm = 10.0.pow(logD)
        return (distanceKm * 1000).coerceIn(10.0, 50000.0)
    }
    
    /**
     * Log-distance model for WiFi (2.4/5 GHz indoor/outdoor)
     * Better than free-space for real-world WiFi
     */
    fun logDistanceWifi(rssiDbm: Int, referenceRssi: Int = -40, 
                        referenceDistanceM: Double = 1.0,
                        pathLossExponent: Double = 3.0): Double {
        // d = d0 * 10^((P0 - P) / (10 * n))
        val distance = referenceDistanceM * 10.0.pow(
            (referenceRssi - rssiDbm) / (10.0 * pathLossExponent))
        return distance.coerceIn(0.5, 500.0)
    }
    
    /**
     * BLE log-distance model with calibrated TX power
     */
    fun logDistanceBle(rssiDbm: Int, txPowerAtOneMeter: Int = -59,
                       pathLossExponent: Double = 2.0): Double {
        val distance = 10.0.pow((txPowerAtOneMeter - rssiDbm) / (10.0 * pathLossExponent))
        return distance.coerceIn(0.1, 100.0)
    }
    
    enum class Environment {
        URBAN, URBAN_LARGE, SUBURBAN, RURAL
    }
}

/**
 * Data class representing a geolocated threat for tactical radar display
 */
data class GeolocatedThreat(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Double, // estimated radius of uncertainty
    val threatLevel: ThreatLevel, // from domain.model.ThreatLevel
    val category: SensorCategory, // from domain.model.SensorCategory
    val label: String, // human-readable description
    val timestamp: Long,
    val signalStrengthDbm: Int? = null,
    val bearing: Float? = null // degrees from north, if determinable
)

/**
 * Singleton class that estimates threat positions from sensor data
 * for tactical radar display
 */
@Singleton
class ThreatGeolocation @Inject constructor() {

    companion object {
        private const val DEFAULT_WIFI_FREQUENCY_MHZ = 2437.0 // Channel 6
        private const val BLE_REFERENCE_RSSI = -59 // RSSI at 1m
        private const val BLE_PATH_LOSS_EXPONENT = 2.0
        
        // Default accuracy estimates
        private const val CELLULAR_ACCURACY_METERS = 350.0
        private const val NETWORK_ACCURACY_METERS = 50.0
        private const val BASELINE_ACCURACY_METERS = 100.0
    }

    /**
     * Convert a list of alerts to geolocated threats for radar display
     */
    fun geolocateThreats(alerts: List<Alert>, userLat: Double, userLng: Double): List<GeolocatedThreat> {
        return alerts.mapNotNull { alert ->
            try {
                geolocateAlert(alert, userLat, userLng)
            } catch (e: Exception) {
                // Log error and skip this alert
                null
            }
        }
    }

    private fun geolocateAlert(alert: Alert, userLat: Double, userLng: Double): GeolocatedThreat? {
        val detailsJson = try {
            JSONObject(alert.detailsJson)
        } catch (e: Exception) {
            return null
        }

        // Determine sensor category from threat type or details
        val category = determineSensorCategory(alert, detailsJson)
        
        // Get coordinates and accuracy based on sensor category
        val (lat, lng, accuracy, signalStrengthDbm, bearing) = when (category) {
            SensorCategory.CELLULAR -> geolocateCellularThreat(detailsJson, userLat, userLng)
            SensorCategory.WIFI -> geolocateWifiThreat(detailsJson, userLat, userLng)
            SensorCategory.BLUETOOTH -> geolocateBluetoothThreat(detailsJson, userLat, userLng)
            SensorCategory.NETWORK -> geolocateNetworkThreat(detailsJson, userLat, userLng)
            SensorCategory.BASELINE -> geolocateBaselineThreat(detailsJson, userLat, userLng)
        }

        return GeolocatedThreat(
            id = alert.id.toString(),
            latitude = lat,
            longitude = lng,
            accuracyMeters = accuracy,
            threatLevel = alert.severity,
            category = category,
            label = generateThreatLabel(alert, category),
            timestamp = alert.timestamp,
            signalStrengthDbm = signalStrengthDbm,
            bearing = bearing
        )
    }

    private fun determineSensorCategory(alert: Alert, detailsJson: JSONObject): SensorCategory {
        // First try to get category from details JSON
        if (detailsJson.has("sensorCategory")) {
            val categoryString = detailsJson.getString("sensorCategory")
            return try {
                SensorCategory.valueOf(categoryString)
            } catch (e: IllegalArgumentException) {
                // Fall back to threat type analysis
                determineCategoryFromThreatType(alert.threatType.toString())
            }
        }

        return determineCategoryFromThreatType(alert.threatType.toString())
    }

    private fun determineCategoryFromThreatType(threatType: String): SensorCategory {
        return when {
            threatType.contains("CELL", ignoreCase = true) ||
            threatType.contains("BTS", ignoreCase = true) ||
            threatType.contains("IMSI", ignoreCase = true) -> SensorCategory.CELLULAR
            
            threatType.contains("WIFI", ignoreCase = true) ||
            threatType.contains("AP", ignoreCase = true) -> SensorCategory.WIFI
            
            threatType.contains("BLE", ignoreCase = true) ||
            threatType.contains("BLUETOOTH", ignoreCase = true) -> SensorCategory.BLUETOOTH
            
            threatType.contains("NETWORK", ignoreCase = true) ||
            threatType.contains("DNS", ignoreCase = true) ||
            threatType.contains("TLS", ignoreCase = true) -> SensorCategory.NETWORK
            
            else -> SensorCategory.BASELINE
        }
    }

    private fun geolocateCellularThreat(
        detailsJson: JSONObject, 
        userLat: Double, 
        userLng: Double
    ): ThreatLocationData {
        // Check if we have cell tower coordinates
        val cellLat = detailsJson.optDouble("cellLatitude", Double.NaN)
        val cellLng = detailsJson.optDouble("cellLongitude", Double.NaN)
        val signalStrengthDbm = if (detailsJson.has("signalStrengthDbm")) {
            detailsJson.getInt("signalStrengthDbm")
        } else null

        return if (!cellLat.isNaN() && !cellLng.isNaN()) {
            ThreatLocationData(
                latitude = cellLat,
                longitude = cellLng,
                accuracyMeters = CELLULAR_ACCURACY_METERS,
                signalStrengthDbm = signalStrengthDbm,
                bearing = null
            )
        } else if (signalStrengthDbm != null) {
            // Use propagation model to estimate distance from user
            val frequencyMhz = detailsJson.optDouble("frequencyMhz", 900.0)
            val distance = if (frequencyMhz < 1500.0) {
                PropagationModels.okumuraHataDistance(
                    rssiDbm = signalStrengthDbm,
                    frequencyMhz = frequencyMhz,
                    environment = PropagationModels.Environment.URBAN
                )
            } else {
                PropagationModels.cost231Distance(
                    rssiDbm = signalStrengthDbm,
                    frequencyMhz = frequencyMhz,
                    isUrban = true
                )
            }
            
            ThreatLocationData(
                latitude = userLat,
                longitude = userLng,
                accuracyMeters = distance,
                signalStrengthDbm = signalStrengthDbm,
                bearing = null
            )
        } else {
            // Fall back to user position with default accuracy
            ThreatLocationData(
                latitude = userLat,
                longitude = userLng,
                accuracyMeters = CELLULAR_ACCURACY_METERS,
                signalStrengthDbm = signalStrengthDbm,
                bearing = null
            )
        }
    }

    private fun geolocateWifiThreat(
        detailsJson: JSONObject, 
        userLat: Double, 
        userLng: Double
    ): ThreatLocationData {
        val rssiDbm = detailsJson.optInt("rssi", -70)
        
        // Use WiFi log-distance model instead of free-space path loss
        val distance = PropagationModels.logDistanceWifi(
            rssiDbm = rssiDbm,
            referenceRssi = -40, // Typical AP TX power at 1m
            referenceDistanceM = 1.0,
            pathLossExponent = 3.0 // Indoor/outdoor environment
        )
        
        return ThreatLocationData(
            latitude = userLat,
            longitude = userLng,
            accuracyMeters = distance,
            signalStrengthDbm = rssiDbm,
            bearing = null // Could be determined from directional antennas if available
        )
    }

    private fun geolocateBluetoothThreat(
        detailsJson: JSONObject, 
        userLat: Double, 
        userLng: Double
    ): ThreatLocationData {
        val rssiDbm = detailsJson.optInt("rssi", -70)
        
        // Use BLE log-distance model with calibrated parameters
        val distance = PropagationModels.logDistanceBle(
            rssiDbm = rssiDbm,
            txPowerAtOneMeter = BLE_REFERENCE_RSSI,
            pathLossExponent = BLE_PATH_LOSS_EXPONENT
        )
        
        return ThreatLocationData(
            latitude = userLat,
            longitude = userLng,
            accuracyMeters = distance,
            signalStrengthDbm = rssiDbm,
            bearing = null
        )
    }

    private fun geolocateNetworkThreat(
        detailsJson: JSONObject, 
        userLat: Double, 
        userLng: Double
    ): ThreatLocationData {
        // Network threats are typically local network attacks
        return ThreatLocationData(
            latitude = userLat,
            longitude = userLng,
            accuracyMeters = NETWORK_ACCURACY_METERS,
            signalStrengthDbm = null,
            bearing = null
        )
    }

    private fun geolocateBaselineThreat(
        detailsJson: JSONObject, 
        userLat: Double, 
        userLng: Double
    ): ThreatLocationData {
        // Check if baseline has stored coordinates
        val baselineLat = detailsJson.optDouble("baselineLatitude", Double.NaN)
        val baselineLng = detailsJson.optDouble("baselineLongitude", Double.NaN)
        val baselineRadius = detailsJson.optDouble("baselineRadius", BASELINE_ACCURACY_METERS)

        return if (!baselineLat.isNaN() && !baselineLng.isNaN()) {
            ThreatLocationData(
                latitude = baselineLat,
                longitude = baselineLng,
                accuracyMeters = baselineRadius,
                signalStrengthDbm = null,
                bearing = null
            )
        } else {
            ThreatLocationData(
                latitude = userLat,
                longitude = userLng,
                accuracyMeters = BASELINE_ACCURACY_METERS,
                signalStrengthDbm = null,
                bearing = null
            )
        }
    }

    private fun generateThreatLabel(alert: Alert, category: SensorCategory): String {
        // Use the alert summary if available, otherwise generate based on category
        return if (alert.summary.isNotBlank()) {
            alert.summary
        } else {
            when (category) {
                SensorCategory.CELLULAR -> "Cellular Threat"
                SensorCategory.WIFI -> "WiFi Threat"
                SensorCategory.BLUETOOTH -> "Bluetooth Threat"
                SensorCategory.NETWORK -> "Network Threat"
                SensorCategory.BASELINE -> "Baseline Anomaly"
            }
        }
    }

    private data class ThreatLocationData(
        val latitude: Double,
        val longitude: Double,
        val accuracyMeters: Double,
        val signalStrengthDbm: Int?,
        val bearing: Float?
    )
}