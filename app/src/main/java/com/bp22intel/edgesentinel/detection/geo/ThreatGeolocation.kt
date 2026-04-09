/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024-2026 BP22 Intel. All Rights Reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use of this software, in whole or in
 * part, is strictly prohibited without prior written permission from
 * BP22 Intel.
 */

package com.bp22intel.edgesentinel.detection.geo

import com.bp22intel.edgesentinel.data.local.dao.KnownTowerDao
import com.bp22intel.edgesentinel.data.local.entity.KnownTowerEntity
import com.bp22intel.edgesentinel.data.sensor.CellInfoCollector
import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.domain.model.CellTower
import com.bp22intel.edgesentinel.domain.model.SensorCategory
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import com.bp22intel.edgesentinel.domain.model.ThreatType
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

// ─────────────────────────────────────────────────────────────────────────────
// Propagation Models
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Advanced propagation models for cellular, WiFi, and BLE distance estimation.
 */
object PropagationModels {

    /** Okumura-Hata model for urban macro-cell (150–1500 MHz). Returns meters. */
    fun okumuraHataDistance(
        rssiDbm: Int,
        frequencyMhz: Double = 900.0,
        baseHeightM: Double = 30.0,
        mobileHeightM: Double = 1.5,
        environment: Environment = Environment.URBAN
    ): Double {
        val txPower = 43.0
        val pathLoss = txPower - rssiDbm

        val aHm = when (environment) {
            Environment.URBAN_LARGE ->
                3.2 * (log10(11.75 * mobileHeightM)).pow(2) - 4.97
            else ->
                (1.1 * log10(frequencyMhz) - 0.7) * mobileHeightM -
                    (1.56 * log10(frequencyMhz) - 0.8)
        }

        val A = 69.55 + 26.16 * log10(frequencyMhz) -
            13.82 * log10(baseHeightM) - aHm
        val B = 44.9 - 6.55 * log10(baseHeightM)

        val correctedPL = when (environment) {
            Environment.SUBURBAN ->
                pathLoss + 2 * (log10(frequencyMhz / 28)).pow(2) + 5.4
            Environment.RURAL ->
                pathLoss + 4.78 * (log10(frequencyMhz)).pow(2) -
                    18.33 * log10(frequencyMhz) + 40.94
            else -> pathLoss.toDouble()
        }

        val logD = (correctedPL - A) / B
        return (10.0.pow(logD) * 1000.0).coerceIn(10.0, 50_000.0)
    }

    /** COST 231-Hata for higher frequencies (1500–2000 MHz, LTE bands). */
    fun cost231Distance(
        rssiDbm: Int,
        frequencyMhz: Double = 1800.0,
        baseHeightM: Double = 30.0,
        mobileHeightM: Double = 1.5,
        isUrban: Boolean = true
    ): Double {
        val txPower = 43.0
        val pathLoss = txPower - rssiDbm

        val aHm = (1.1 * log10(frequencyMhz) - 0.7) * mobileHeightM -
            (1.56 * log10(frequencyMhz) - 0.8)
        val cM = if (isUrban) 3.0 else 0.0

        val A = 46.3 + 33.9 * log10(frequencyMhz) -
            13.82 * log10(baseHeightM) - aHm + cM
        val B = 44.9 - 6.55 * log10(baseHeightM)

        val logD = (pathLoss - A) / B
        return (10.0.pow(logD) * 1000.0).coerceIn(10.0, 50_000.0)
    }

    /** Log-distance model for WiFi (2.4/5 GHz). */
    fun logDistanceWifi(
        rssiDbm: Int,
        referenceRssi: Int = -40,
        referenceDistanceM: Double = 1.0,
        pathLossExponent: Double = 3.0
    ): Double {
        val d = referenceDistanceM *
            10.0.pow((referenceRssi - rssiDbm) / (10.0 * pathLossExponent))
        return d.coerceIn(0.5, 500.0)
    }

    /** BLE log-distance model with calibrated TX power. */
    fun logDistanceBle(
        rssiDbm: Int,
        txPowerAtOneMeter: Int = -59,
        pathLossExponent: Double = 2.0
    ): Double {
        val d = 10.0.pow((txPowerAtOneMeter - rssiDbm) / (10.0 * pathLossExponent))
        return d.coerceIn(0.1, 100.0)
    }

    enum class Environment { URBAN, URBAN_LARGE, SUBURBAN, RURAL }
}

// ─────────────────────────────────────────────────────────────────────────────
// Geo Math Utilities
// ─────────────────────────────────────────────────────────────────────────────

private object GeoMath {
    private const val EARTH_RADIUS_M = 6_371_000.0

    /** Haversine distance in meters. */
    fun haversineM(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLng / 2).pow(2)
        return 2.0 * EARTH_RADIUS_M * asin(sqrt(a))
    }

    /** Initial bearing from point 1 to point 2, in degrees [0, 360). */
    fun bearing(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val φ1 = Math.toRadians(lat1)
        val φ2 = Math.toRadians(lat2)
        val Δλ = Math.toRadians(lng2 - lng1)
        val y = sin(Δλ) * cos(φ2)
        val x = cos(φ1) * sin(φ2) - sin(φ1) * cos(φ2) * cos(Δλ)
        return (Math.toDegrees(atan2(y, x)) + 360.0) % 360.0
    }

    /** Destination point given start, bearing (degrees), and distance (meters). */
    fun destination(lat: Double, lng: Double, bearingDeg: Double, distanceM: Double): Pair<Double, Double> {
        val φ1 = Math.toRadians(lat)
        val λ1 = Math.toRadians(lng)
        val θ = Math.toRadians(bearingDeg)
        val δ = distanceM / EARTH_RADIUS_M

        val φ2 = asin(sin(φ1) * cos(δ) + cos(φ1) * sin(δ) * cos(θ))
        val λ2 = λ1 + atan2(sin(θ) * sin(δ) * cos(φ1), cos(δ) - sin(φ1) * sin(φ2))
        return Pair(Math.toDegrees(φ2), Math.toDegrees(λ2))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Data Classes
// ─────────────────────────────────────────────────────────────────────────────

/** A single geo-reading of a threat from a known user position. */
data class GeoReading(
    val userLat: Double,
    val userLng: Double,
    val rssi: Int,
    val timestamp: Long,
    val estimatedDistanceM: Double
)

/** Geolocation technique used, for provenance/debugging. */
enum class GeoTechnique {
    KNOWN_TOWER_DB,
    MULTILATERATION,
    TIME_SERIES_TRIANGULATION,
    SIGNAL_GRADIENT_BEARING,
    RSSI_PROPAGATION,
    BLE_TRAJECTORY,
    CONFIDENCE_FUSION
}

/** Intermediate position estimate from a single technique. */
private data class PositionEstimate(
    val latitude: Double,
    val longitude: Double,
    val accuracyM: Double,
    val bearingDeg: Double? = null,
    val technique: GeoTechnique
)

/** Output data class — same public interface as before. */
data class GeolocatedThreat(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Double,
    val threatLevel: ThreatLevel,
    val category: SensorCategory,
    val label: String,
    val timestamp: Long,
    val signalStrengthDbm: Int? = null,
    val bearing: Float? = null
)

// ─────────────────────────────────────────────────────────────────────────────
// Main Geolocation Engine
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Stateful singleton that estimates threat positions using every available
 * signal: known-tower DB lookups, multilateration from visible neighbor
 * cells, time-series triangulation as the user moves, signal-gradient
 * bearing, environment-aware WiFi/BLE models, and confidence-weighted
 * fusion when multiple techniques produce estimates for the same threat.
 */
@Singleton
class ThreatGeolocation @Inject constructor(
    private val knownTowerDao: KnownTowerDao,
    private val cellInfoCollector: CellInfoCollector
) {

    // ── Constants ────────────────────────────────────────────────────────
    companion object {
        private const val READING_MAX_AGE_MS = 10 * 60 * 1000L          // 10 min
        private const val MIN_MOVEMENT_M = 50.0                          // for triangulation
        private const val TOWER_CACHE_TTL_MS = 5 * 60 * 1000L           // 5 min
        private const val NEIGHBOR_SEARCH_RADIUS_DEG = 0.15             // ~16 km box

        // Accuracy floors per technique
        private const val ACC_KNOWN_TOWER = 50.0
        private const val ACC_MULTILAT_3PLUS = 150.0
        private const val ACC_MULTILAT_2 = 300.0
        private const val ACC_TIMESERIES_3PLUS = 100.0
        private const val ACC_TIMESERIES_2 = 200.0
        private const val ACC_GRADIENT_BEARING = 500.0
        private const val ACC_RSSI_CELL = 400.0
        private const val ACC_RSSI_WIFI = 40.0
        private const val ACC_RSSI_BLE = 10.0
        private const val ACC_BLE_TRAJECTORY = 15.0

        // WiFi path-loss exponents by environment hint
        private const val WIFI_PLE_OPEN = 2.0
        private const val WIFI_PLE_OFFICE = 3.0
        private const val WIFI_PLE_DENSE_URBAN = 3.5
    }

    // ── State ────────────────────────────────────────────────────────────

    /** Per-threat reading history for time-series techniques. */
    private val readingHistory = mutableMapOf<String, MutableList<GeoReading>>()

    /** Simple LRU cache for tower lookups to avoid repeated DB hits. */
    private data class TowerCacheEntry(val tower: KnownTowerEntity?, val ts: Long)
    private val towerCache = mutableMapOf<String, TowerCacheEntry>()

    // ── Public API ───────────────────────────────────────────────────────

    /**
     * Convert a list of alerts to geolocated threats.
     *
     * Suspend because it may hit the known-tower Room database.
     * Called from a coroutine in ThreatMapViewModel — no call-site change needed.
     */
    suspend fun geolocateThreats(
        alerts: List<Alert>,
        userLat: Double,
        userLng: Double
    ): List<GeolocatedThreat> {
        if (userLat == 0.0 && userLng == 0.0) return emptyList()

        // Snapshot visible cells once per call (used for multilateration).
        val visibleCells = try { cellInfoCollector.getCurrentCellInfo() } catch (_: Exception) { emptyList() }

        pruneReadingHistory()

        return alerts.mapNotNull { alert ->
            try {
                geolocateAlert(alert, userLat, userLng, visibleCells)
            } catch (_: Exception) {
                null
            }
        }
    }

    // ── Per-alert pipeline ───────────────────────────────────────────────

    private suspend fun geolocateAlert(
        alert: Alert,
        userLat: Double,
        userLng: Double,
        visibleCells: List<CellTower>
    ): GeolocatedThreat {
        val details = try { JSONObject(alert.detailsJson) } catch (_: Exception) { JSONObject() }
        val category = determineSensorCategory(alert)
        val signalStrength = extractSignalStrength(details)
        val threatId = alert.id.toString()

        // Collect estimates from every applicable technique.
        val estimates = mutableListOf<PositionEstimate>()

        // ── 1. Known tower DB lookup (cellular only) ─────────────────
        if (category == SensorCategory.CELLULAR) {
            lookupKnownTower(details)?.let { tower ->
                estimates += PositionEstimate(
                    latitude = tower.latitude,
                    longitude = tower.longitude,
                    accuracyM = ACC_KNOWN_TOWER,
                    bearingDeg = GeoMath.bearing(userLat, userLng, tower.latitude, tower.longitude),
                    technique = GeoTechnique.KNOWN_TOWER_DB
                )
            }
        }

        // ── 2. Multilateration from neighbor cells ───────────────────
        if (category == SensorCategory.CELLULAR && estimates.none { it.technique == GeoTechnique.KNOWN_TOWER_DB }) {
            multilaterateFromNeighbors(userLat, userLng, visibleCells, signalStrength, details)?.let {
                estimates += it
            }
        }

        // ── 3 & 4. Time-series triangulation + gradient bearing ──────
        if (signalStrength != null) {
            val distanceM = estimateDistance(signalStrength, category, details)
            recordReading(threatId, userLat, userLng, signalStrength, distanceM)

            timeSeriesTriangulate(threatId, category)?.let { estimates += it }
            gradientBearing(threatId, userLat, userLng, distanceM)?.let { estimates += it }
        }

        // ── 5. WiFi-specific ─────────────────────────────────────────
        if (category == SensorCategory.WIFI && signalStrength != null) {
            val ple = wifiPathLossExponent(details)
            val dist = PropagationModels.logDistanceWifi(
                rssiDbm = signalStrength,
                pathLossExponent = ple
            )
            // Without bearing we place at user + dist in a heuristic direction.
            // If gradient bearing exists it will be fused; otherwise use hash-based angle.
            val fallbackBearing = (threatId.hashCode() and 0x7FFFFFFF) % 360
            val (lat, lng) = GeoMath.destination(userLat, userLng, fallbackBearing.toDouble(), dist)
            estimates += PositionEstimate(lat, lng, ACC_RSSI_WIFI, fallbackBearing.toDouble(), GeoTechnique.RSSI_PROPAGATION)
        }

        // ── 6. BLE tracker trajectory ────────────────────────────────
        if (category == SensorCategory.BLUETOOTH) {
            bleTrajectoryEstimate(threatId, userLat, userLng, signalStrength)?.let {
                estimates += it
            }
        }

        // ── 7. Fallback: RSSI propagation model (cell) ──────────────
        if (category == SensorCategory.CELLULAR && estimates.isEmpty()) {
            val dist = if (signalStrength != null) estimateDistance(signalStrength, category, details) else 500.0
            val fallbackBearing = (threatId.hashCode() and 0x7FFFFFFF) % 360
            val (lat, lng) = GeoMath.destination(userLat, userLng, fallbackBearing.toDouble(), dist)
            estimates += PositionEstimate(lat, lng, ACC_RSSI_CELL, fallbackBearing.toDouble(), GeoTechnique.RSSI_PROPAGATION)
        }

        // ── Catch-all for NETWORK / BASELINE / empty ─────────────────
        if (estimates.isEmpty()) {
            val dist = when (category) {
                SensorCategory.WIFI -> 50.0
                SensorCategory.BLUETOOTH -> 15.0
                SensorCategory.NETWORK -> 30.0
                else -> 200.0
            }
            val fallbackBearing = (threatId.hashCode() and 0x7FFFFFFF) % 360
            val (lat, lng) = GeoMath.destination(userLat, userLng, fallbackBearing.toDouble(), dist)
            estimates += PositionEstimate(lat, lng, dist * 0.5, fallbackBearing.toDouble(), GeoTechnique.RSSI_PROPAGATION)
        }

        // ── 8. Confidence-weighted fusion ────────────────────────────
        val fused = fuseEstimates(estimates)

        return GeolocatedThreat(
            id = threatId,
            latitude = fused.latitude,
            longitude = fused.longitude,
            accuracyMeters = fused.accuracyM,
            threatLevel = alert.severity,
            category = category,
            label = generateThreatLabel(alert, category),
            timestamp = alert.timestamp,
            signalStrengthDbm = signalStrength,
            bearing = fused.bearingDeg?.toFloat()
        )
    }

    // ── Technique Implementations ────────────────────────────────────────

    /**
     * 1. Look up the alert's cell identity in the 2.16M known-tower DB.
     *    Returns the entity if found, using a short-lived cache to avoid
     *    repeated Room queries within the same scan cycle.
     */
    private suspend fun lookupKnownTower(details: JSONObject): KnownTowerEntity? {
        val mcc = details.optInt("mcc", 0)
        val mnc = details.optInt("mnc", 0)
        val lac = details.optInt("lac", details.optInt("tac", 0))
        val cid = details.optInt("cid", 0)
        if (mcc == 0 || cid == 0) return null

        val key = "$mcc:$mnc:$lac:$cid"
        val cached = towerCache[key]
        if (cached != null && System.currentTimeMillis() - cached.ts < TOWER_CACHE_TTL_MS) {
            return cached.tower
        }

        val entity = knownTowerDao.findTower(mcc, mnc, lac, cid)
        towerCache[key] = TowerCacheEntry(entity, System.currentTimeMillis())
        return entity
    }

    /**
     * 2. Multilateration — use positions of visible KNOWN neighbor towers
     *    weighted by RSSI to estimate user position, then infer where an
     *    UNKNOWN tower must be relative to that weighted centroid.
     */
    private suspend fun multilaterateFromNeighbors(
        userLat: Double,
        userLng: Double,
        visibleCells: List<CellTower>,
        threatRssi: Int?,
        details: JSONObject
    ): PositionEstimate? {
        // Resolve known towers among visible cells.
        data class ResolvedCell(val tower: KnownTowerEntity, val rssi: Int)

        val resolved = mutableListOf<ResolvedCell>()
        for (cell in visibleCells) {
            if (cell.mcc == 0 || cell.cid == 0) continue
            val key = "${cell.mcc}:${cell.mnc}:${cell.lacTac}:${cell.cid}"
            val cached = towerCache[key]
            val entity = if (cached != null && System.currentTimeMillis() - cached.ts < TOWER_CACHE_TTL_MS) {
                cached.tower
            } else {
                val e = knownTowerDao.findTower(cell.mcc, cell.mnc, cell.lacTac, cell.cid)
                towerCache[key] = TowerCacheEntry(e, System.currentTimeMillis())
                e
            }
            if (entity != null) {
                resolved += ResolvedCell(entity, cell.signalStrength)
            }
        }

        if (resolved.size < 2) return null

        // Weighted centroid of known towers (weight = 10^(RSSI/20)).
        var sumLat = 0.0; var sumLng = 0.0; var sumW = 0.0
        for (rc in resolved) {
            val w = 10.0.pow(rc.rssi / 20.0)
            sumLat += rc.tower.latitude * w
            sumLng += rc.tower.longitude * w
            sumW += w
        }
        val centroidLat = sumLat / sumW
        val centroidLng = sumLng / sumW

        // The unknown tower is roughly opposite the centroid from the user.
        // Estimate distance from the threat's RSSI.
        val dist = if (threatRssi != null) {
            estimateDistance(threatRssi, SensorCategory.CELLULAR, details)
        } else {
            500.0
        }

        val bearingToCentroid = GeoMath.bearing(userLat, userLng, centroidLat, centroidLng)
        // Opposite direction — unknown tower is on the other side of the user from the cluster.
        val oppositeBearing = (bearingToCentroid + 180.0) % 360.0
        val (estLat, estLng) = GeoMath.destination(userLat, userLng, oppositeBearing, dist)

        val accuracy = if (resolved.size >= 3) ACC_MULTILAT_3PLUS else ACC_MULTILAT_2
        return PositionEstimate(estLat, estLng, accuracy, oppositeBearing, GeoTechnique.MULTILATERATION)
    }

    /**
     * 3. Time-series triangulation — least-squares circle intersection
     *    when we have 2+ readings from spatially distinct user positions.
     */
    private fun timeSeriesTriangulate(
        threatId: String,
        category: SensorCategory
    ): PositionEstimate? {
        val readings = readingHistory[threatId] ?: return null

        // Need at least 2 readings with sufficient user movement.
        val spatiallyDistinct = pickDistinctReadings(readings)
        if (spatiallyDistinct.size < 2) return null

        // Weighted least-squares centroid from circle intersections.
        // Each reading defines a circle (userPos, estimatedDistance).
        // We minimize Σ w_i * (dist(P, userPos_i) - r_i)^2 via iterative weighted centroid.
        var estLat = spatiallyDistinct.map { it.userLat }.average()
        var estLng = spatiallyDistinct.map { it.userLng }.average()

        // 3 iterations of reweighted centroid — converges quickly for this scale.
        repeat(3) {
            var sLat = 0.0; var sLng = 0.0; var sW = 0.0
            for (r in spatiallyDistinct) {
                val d = GeoMath.haversineM(estLat, estLng, r.userLat, r.userLng)
                val target = r.estimatedDistanceM
                // Pull estimate toward the circle edge.
                val bearing = GeoMath.bearing(r.userLat, r.userLng, estLat, estLng)
                val (pLat, pLng) = GeoMath.destination(r.userLat, r.userLng, bearing, target)
                val w = 1.0 / (abs(d - target) + 1.0) // closer to circle → more weight
                sLat += pLat * w
                sLng += pLng * w
                sW += w
            }
            if (sW > 0) { estLat = sLat / sW; estLng = sLng / sW }
        }

        val bearingDeg = GeoMath.bearing(
            spatiallyDistinct.last().userLat, spatiallyDistinct.last().userLng,
            estLat, estLng
        )
        val accuracy = if (spatiallyDistinct.size >= 3) ACC_TIMESERIES_3PLUS else ACC_TIMESERIES_2
        return PositionEstimate(estLat, estLng, accuracy, bearingDeg, GeoTechnique.TIME_SERIES_TRIANGULATION)
    }

    /**
     * 4. Signal-strength gradient bearing — if RSSI changed as the user
     *    moved, the direction of increasing signal points toward the threat.
     */
    private fun gradientBearing(
        threatId: String,
        userLat: Double,
        userLng: Double,
        currentDistM: Double
    ): PositionEstimate? {
        val readings = readingHistory[threatId] ?: return null
        if (readings.size < 2) return null

        val prev = readings[readings.size - 2]
        val curr = readings.last()

        val userMovedM = GeoMath.haversineM(prev.userLat, prev.userLng, curr.userLat, curr.userLng)
        if (userMovedM < 20.0) return null // not enough movement to trust gradient

        val rssiDelta = curr.rssi - prev.rssi // positive = getting closer
        val moveBearing = GeoMath.bearing(prev.userLat, prev.userLng, curr.userLat, curr.userLng)

        // If signal got stronger, threat is roughly in our movement direction.
        // If weaker, it's behind us.
        val threatBearing = if (rssiDelta >= 0) moveBearing else (moveBearing + 180.0) % 360.0

        val (lat, lng) = GeoMath.destination(userLat, userLng, threatBearing, currentDistM)
        return PositionEstimate(lat, lng, ACC_GRADIENT_BEARING, threatBearing, GeoTechnique.SIGNAL_GRADIENT_BEARING)
    }

    /**
     * 5. WiFi path-loss exponent selection based on environment hint.
     */
    private fun wifiPathLossExponent(details: JSONObject): Double {
        return when (details.optString("environment", "").lowercase()) {
            "open", "outdoor" -> WIFI_PLE_OPEN
            "office", "indoor" -> WIFI_PLE_OFFICE
            "dense", "urban" -> WIFI_PLE_DENSE_URBAN
            else -> WIFI_PLE_OFFICE // safe default
        }
    }

    /**
     * 6. BLE tracker trajectory — a tracker following the user is at
     *    approximately the user's past positions. Place it at the first
     *    detection point (where the tracker started following).
     */
    private fun bleTrajectoryEstimate(
        threatId: String,
        userLat: Double,
        userLng: Double,
        rssi: Int?
    ): PositionEstimate? {
        val readings = readingHistory[threatId]

        if (readings != null && readings.size >= 2) {
            // Tracker has been following — place it at the earliest reading position.
            val first = readings.first()
            val bearing = GeoMath.bearing(userLat, userLng, first.userLat, first.userLng)
            return PositionEstimate(
                first.userLat, first.userLng,
                ACC_BLE_TRAJECTORY, bearing,
                GeoTechnique.BLE_TRAJECTORY
            )
        }

        // Single reading — use BLE distance model.
        val dist = if (rssi != null) PropagationModels.logDistanceBle(rssi) else 15.0
        val fallbackBearing = (threatId.hashCode() and 0x7FFFFFFF) % 360
        val (lat, lng) = GeoMath.destination(userLat, userLng, fallbackBearing.toDouble(), dist)
        return PositionEstimate(lat, lng, ACC_RSSI_BLE, fallbackBearing.toDouble(), GeoTechnique.RSSI_PROPAGATION)
    }

    // ── 8. Confidence-Weighted Fusion ────────────────────────────────────

    /**
     * Fuse multiple position estimates weighted by inverse accuracy
     * (more accurate techniques contribute more). If only one estimate
     * exists, return it directly.
     */
    private fun fuseEstimates(estimates: List<PositionEstimate>): PositionEstimate {
        if (estimates.size == 1) return estimates[0]

        var sumLat = 0.0; var sumLng = 0.0; var sumW = 0.0
        var bestBearing: Double? = null
        var bestBearingAcc = Double.MAX_VALUE

        for (e in estimates) {
            val w = 1.0 / e.accuracyM // inverse accuracy weighting
            sumLat += e.latitude * w
            sumLng += e.longitude * w
            sumW += w
            // Keep bearing from the most accurate technique that provides one.
            if (e.bearingDeg != null && e.accuracyM < bestBearingAcc) {
                bestBearing = e.bearingDeg
                bestBearingAcc = e.accuracyM
            }
        }

        val fusedLat = sumLat / sumW
        val fusedLng = sumLng / sumW

        // Combined accuracy: harmonic-ish combination (always better than worst).
        val fusedAcc = 1.0 / sumW // = 1 / Σ(1/acc_i)

        return PositionEstimate(
            latitude = fusedLat,
            longitude = fusedLng,
            accuracyM = fusedAcc.coerceAtLeast(10.0), // floor at 10 m
            bearingDeg = bestBearing,
            technique = GeoTechnique.CONFIDENCE_FUSION
        )
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private fun determineSensorCategory(alert: Alert): SensorCategory = when (alert.threatType) {
        ThreatType.FAKE_BTS,
        ThreatType.NETWORK_DOWNGRADE,
        ThreatType.SILENT_SMS,
        ThreatType.CIPHER_ANOMALY,
        ThreatType.SIGNAL_ANOMALY,
        ThreatType.NR_ANOMALY -> SensorCategory.CELLULAR
        ThreatType.TRACKING_PATTERN -> SensorCategory.BLUETOOTH
    }

    private fun extractSignalStrength(details: JSONObject): Int? {
        val v = details.optInt("signalStrength", Int.MIN_VALUE).let {
            if (it == Int.MIN_VALUE) details.optInt("signalStrengthDbm", Int.MIN_VALUE) else it
        }.let {
            if (it == Int.MIN_VALUE) details.optInt("rssi", Int.MIN_VALUE) else it
        }
        return if (v == Int.MIN_VALUE) null else v
    }

    /** Estimate distance in meters based on signal strength and category. */
    private fun estimateDistance(rssi: Int, category: SensorCategory, details: JSONObject): Double {
        return when (category) {
            SensorCategory.CELLULAR -> {
                val freq = details.optDouble("frequencyMhz", 900.0)
                if (freq < 1500.0) PropagationModels.okumuraHataDistance(rssi, freq)
                else PropagationModels.cost231Distance(rssi, freq)
            }
            SensorCategory.WIFI -> {
                PropagationModels.logDistanceWifi(rssi, pathLossExponent = wifiPathLossExponent(details))
            }
            SensorCategory.BLUETOOTH -> PropagationModels.logDistanceBle(rssi)
            else -> 200.0
        }
    }

    /** Record a new reading for time-series techniques. */
    private fun recordReading(
        threatId: String,
        userLat: Double,
        userLng: Double,
        rssi: Int,
        distM: Double
    ) {
        val list = readingHistory.getOrPut(threatId) { mutableListOf() }
        list += GeoReading(userLat, userLng, rssi, System.currentTimeMillis(), distM)
        // Cap at ~60 readings per threat.
        if (list.size > 60) list.removeAt(0)
    }

    /** Prune readings older than [READING_MAX_AGE_MS]. */
    private fun pruneReadingHistory() {
        val cutoff = System.currentTimeMillis() - READING_MAX_AGE_MS
        val iter = readingHistory.iterator()
        while (iter.hasNext()) {
            val entry = iter.next()
            entry.value.removeAll { it.timestamp < cutoff }
            if (entry.value.isEmpty()) iter.remove()
        }
    }

    /**
     * From a list of readings, pick those where the user moved ≥[MIN_MOVEMENT_M]
     * from the previous picked reading.
     */
    private fun pickDistinctReadings(readings: List<GeoReading>): List<GeoReading> {
        if (readings.isEmpty()) return emptyList()
        val result = mutableListOf(readings.first())
        for (r in readings) {
            val last = result.last()
            if (GeoMath.haversineM(last.userLat, last.userLng, r.userLat, r.userLng) >= MIN_MOVEMENT_M) {
                result += r
            }
        }
        return result
    }

    private fun generateThreatLabel(alert: Alert, category: SensorCategory): String {
        return if (alert.summary.isNotBlank()) alert.summary
        else when (category) {
            SensorCategory.CELLULAR -> "Cellular Threat"
            SensorCategory.WIFI -> "WiFi Threat"
            SensorCategory.BLUETOOTH -> "Bluetooth Threat"
            SensorCategory.NETWORK -> "Network Threat"
            SensorCategory.BASELINE -> "Baseline Anomaly"
        }
    }
}
