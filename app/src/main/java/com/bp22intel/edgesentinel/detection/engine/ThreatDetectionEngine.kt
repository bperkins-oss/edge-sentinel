/*
 * Edge Sentinel — Threat Detection Engine
 * Copyright (C) 2024-2026 BP22 Intel. All Rights Reserved.
 * Proprietary and confidential.
 *
 * Clean-room implementation. Detection-to-category mapping designed from
 * published IMSI-catcher detection research (see ThreatScorer for citations).
 * No third-party code.
 */

package com.bp22intel.edgesentinel.detection.engine

import com.bp22intel.edgesentinel.baseline.BaselineManager
import com.bp22intel.edgesentinel.detection.detectors.ThreatDetector
import com.bp22intel.edgesentinel.detection.scoring.ThreatScorer
import com.bp22intel.edgesentinel.domain.model.CellTower
import com.bp22intel.edgesentinel.domain.model.Confidence
import com.bp22intel.edgesentinel.domain.model.DetectionResult
import com.bp22intel.edgesentinel.domain.model.DetectionSensitivity
import com.bp22intel.edgesentinel.domain.model.ThreatType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central detection engine that orchestrates all threat detectors and scoring.
 *
 * Runs all registered [ThreatDetector] implementations in parallel, collects
 * non-null results, maps them to the six scoring categories defined by
 * [ThreatScorer], and returns results sorted by severity (highest first).
 */
@Singleton
class ThreatDetectionEngine @Inject constructor(
    private val detectors: Set<@JvmSuppressWildcards ThreatDetector>,
    private val scorer: ThreatScorer,
    private val baselineManager: BaselineManager
) {

    /**
     * Run all detectors against the current cell environment.
     *
     * @param cells      Currently visible cell towers.
     * @param history    Previously observed cell towers for baseline comparison.
     * @param sensitivity Detection sensitivity level that adjusts scoring thresholds.
     * @param latitude   Current GPS latitude (optional; enables baseline comparison).
     * @param longitude  Current GPS longitude (optional; enables baseline comparison).
     * @return List of [DetectionResult] sorted by score descending (highest threat first).
     */
    suspend fun runScan(
        cells: List<CellTower>,
        history: List<CellTower>,
        sensitivity: DetectionSensitivity = DetectionSensitivity.MEDIUM,
        latitude: Double? = null,
        longitude: Double? = null
    ): List<DetectionResult> = coroutineScope {
        // Run all detectors in parallel
        val detectorResults = detectors.map { detector ->
            async {
                try {
                    detector.analyze(cells, history)
                } catch (_: Exception) {
                    // Individual detector failure must not crash the scan
                    null
                }
            }
        }

        // Run baseline comparison concurrently with detectors
        val baselineResult = if (latitude != null && longitude != null) {
            async {
                try {
                    baselineManager.processObservation(latitude, longitude, cells)
                } catch (_: Exception) {
                    null
                }
            }
        } else null

        val results = detectorResults.awaitAll().filterNotNull().toMutableList()
        val anomaly = baselineResult?.await()

        // Convert a significant baseline anomaly into a DetectionResult
        if (anomaly != null && !anomaly.isNewLocation && anomaly.compositeScore > 0.3) {
            results.add(
                DetectionResult(
                    threatType = ThreatType.SIGNAL_ANOMALY,
                    score = anomaly.compositeScore * 5.0,
                    confidence = when {
                        anomaly.confidence.minObservations >= 20 -> Confidence.HIGH
                        anomaly.confidence.minObservations >= 10 -> Confidence.MEDIUM
                        else -> Confidence.LOW
                    },
                    summary = "RF environment deviates from learned baseline (%.0f%% anomaly)".format(
                        anomaly.compositeScore * 100
                    ),
                    details = anomaly.details
                )
            )
        }

        if (results.isEmpty()) return@coroutineScope emptyList()

        // ---- Build category indicators from detection results ----
        val indicators = mutableMapOf<String, Double>()
        for (result in results) {
            mapToCategories(result, indicators)
        }

        // Fold baseline anomaly into signal-anomaly category
        if (anomaly != null && !anomaly.isNewLocation) {
            accumulateMax(
                indicators,
                ThreatScorer.KEY_SIGNAL_ANOMALY,
                anomaly.compositeScore
            )
        }

        // Compute composite score (determines overall threat level)
        scorer.calculateScore(indicators, sensitivity)

        // Return individual results ordered by severity
        results.sortedByDescending { it.score }
    }

    // -----------------------------------------------------------------
    // Category mapping
    // -----------------------------------------------------------------

    /**
     * Map a single [DetectionResult] into the six-category scoring model.
     *
     * Each [ThreatType] contributes to one or more categories based on the
     * nature of the detection.  Values are accumulated via max (not sum)
     * so that multiple detectors confirming the same signal don't
     * artificially inflate the score.
     */
    private fun mapToCategories(
        result: DetectionResult,
        indicators: MutableMap<String, Double>
    ) {
        when (result.threatType) {

            ThreatType.FAKE_BTS -> {
                // Unknown cell / strong-signal anomaly → signal + tower categories
                if (result.details.keys.any { it.startsWith("unknown_cid") }) {
                    accumulateMax(indicators, ThreatScorer.KEY_TOWER_BEHAVIOR, 0.9)
                }
                if (result.details.containsKey("missing_neighbors")) {
                    accumulateMax(indicators, ThreatScorer.KEY_TOWER_BEHAVIOR, 0.8)
                }
                if (result.details.keys.any { it.startsWith("strong_signal") }) {
                    accumulateMax(indicators, ThreatScorer.KEY_SIGNAL_ANOMALY, 0.85)
                }
            }

            ThreatType.NETWORK_DOWNGRADE -> {
                // RAT / cipher downgrade → protocol violation + network integrity
                accumulateMax(
                    indicators,
                    ThreatScorer.KEY_PROTOCOL_VIOLATION,
                    (result.score / 5.0).coerceAtMost(1.0)
                )
                accumulateMax(indicators, ThreatScorer.KEY_NETWORK_INTEGRITY, 0.7)
            }

            ThreatType.TRACKING_PATTERN -> {
                // LAC oscillation, rapid reselection → tower + temporal categories
                if (result.details.containsKey("unknown_lac")) {
                    accumulateMax(indicators, ThreatScorer.KEY_TOWER_BEHAVIOR, 0.8)
                }
                if (result.details.containsKey("rapid_reselection")) {
                    accumulateMax(indicators, ThreatScorer.KEY_TEMPORAL_PATTERN, 0.9)
                }
                if (result.details.containsKey("lac_oscillation")) {
                    accumulateMax(indicators, ThreatScorer.KEY_TEMPORAL_PATTERN, 0.85)
                }
                if (result.details.containsKey("short_duration")) {
                    accumulateMax(indicators, ThreatScorer.KEY_TEMPORAL_PATTERN, 0.7)
                }
            }

            ThreatType.SILENT_SMS -> {
                // Silent SMS / Type-0 → protocol violation
                accumulateMax(indicators, ThreatScorer.KEY_PROTOCOL_VIOLATION, 0.6)
            }

            ThreatType.CIPHER_ANOMALY -> {
                // Cipher mode issues → protocol violation + network integrity
                accumulateMax(
                    indicators,
                    ThreatScorer.KEY_PROTOCOL_VIOLATION,
                    (result.score / 5.0).coerceAtMost(1.0)
                )
                accumulateMax(indicators, ThreatScorer.KEY_NETWORK_INTEGRITY, 0.8)
            }

            ThreatType.SIGNAL_ANOMALY -> {
                // RF fingerprint mismatch → signal anomaly
                accumulateMax(
                    indicators,
                    ThreatScorer.KEY_SIGNAL_ANOMALY,
                    (result.score / 5.0).coerceAtMost(1.0)
                )
            }

            ThreatType.NR_ANOMALY -> {
                // 5G / NR-specific anomalies distribute across multiple categories
                if (result.details.keys.any { it.startsWith("nr_unknown_nci") }) {
                    accumulateMax(indicators, ThreatScorer.KEY_TOWER_BEHAVIOR, 0.9)
                }
                if (result.details.keys.any { it.startsWith("nr_strong_signal") }) {
                    accumulateMax(indicators, ThreatScorer.KEY_SIGNAL_ANOMALY, 0.85)
                }
                if (result.details.containsKey("nr_missing_neighbors")) {
                    accumulateMax(indicators, ThreatScorer.KEY_TOWER_BEHAVIOR, 0.8)
                }
                if (result.details.keys.any { it.contains("downgrade") || it.contains("fallback") }) {
                    accumulateMax(
                        indicators,
                        ThreatScorer.KEY_NETWORK_INTEGRITY,
                        (result.score / 5.0).coerceAtMost(1.0)
                    )
                }
                if (result.details.keys.any { it.contains("oscillation") || it.contains("rapid_reselection") }) {
                    accumulateMax(indicators, ThreatScorer.KEY_TEMPORAL_PATTERN, 0.8)
                }
                if (result.details.keys.any { it.startsWith("nr_signal_jump") || it.startsWith("nr_uniform_signals") }) {
                    accumulateMax(indicators, ThreatScorer.KEY_SIGNAL_ANOMALY, 0.75)
                }
            }
        }
    }

    /**
     * Set [key] to the maximum of its current value and [value].
     * Ensures multiple detectors confirming the same category don't
     * over-inflate the score — the strongest signal wins.
     */
    private fun accumulateMax(
        map: MutableMap<String, Double>,
        key: String,
        value: Double
    ) {
        map[key] = maxOf(map[key] ?: 0.0, value)
    }
}
