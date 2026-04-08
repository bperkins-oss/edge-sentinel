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

package com.bp22intel.edgesentinel.detection.engine

import com.bp22intel.edgesentinel.detection.detectors.ThreatDetector
import com.bp22intel.edgesentinel.detection.scoring.ThreatScorer
import com.bp22intel.edgesentinel.domain.model.CellTower
import com.bp22intel.edgesentinel.domain.model.DetectionResult
import com.bp22intel.edgesentinel.domain.model.DetectionSensitivity
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central detection engine that orchestrates all threat detectors and scoring.
 *
 * Runs all registered [ThreatDetector] implementations in parallel, collects
 * non-null results, computes a composite threat score via [ThreatScorer],
 * and returns results sorted by severity (highest threat first).
 */
@Singleton
class ThreatDetectionEngine @Inject constructor(
    private val detectors: Set<@JvmSuppressWildcards ThreatDetector>,
    private val scorer: ThreatScorer
) {

    /**
     * Run all detectors against the current cell environment.
     *
     * @param cells Currently visible cell towers.
     * @param history Previously observed cell towers for baseline comparison.
     * @param sensitivity Detection sensitivity level that adjusts scoring thresholds.
     * @return List of [DetectionResult] sorted by score descending (highest threat first).
     */
    suspend fun runScan(
        cells: List<CellTower>,
        history: List<CellTower>,
        sensitivity: DetectionSensitivity = DetectionSensitivity.MEDIUM
    ): List<DetectionResult> = coroutineScope {
        // Run all detectors in parallel
        val results = detectors.map { detector ->
            async {
                try {
                    detector.analyze(cells, history)
                } catch (e: Exception) {
                    // Individual detector failure should not crash the scan
                    null
                }
            }
        }.awaitAll().filterNotNull()

        if (results.isEmpty()) return@coroutineScope emptyList()

        // Build indicator map from all detection results for composite scoring
        val indicators = mutableMapOf<String, Double>()
        for (result in results) {
            // Map detection results to SnoopSnitch-style coefficients
            mapToCoefficients(result, indicators)
        }

        // Compute composite score (used for overall threat level assessment)
        scorer.calculateScore(indicators, sensitivity)

        // Return individual results sorted by score descending
        results.sortedByDescending { it.score }
    }

    /**
     * Map a detector's result to SnoopSnitch-style scoring coefficients.
     */
    private fun mapToCoefficients(result: DetectionResult, indicators: MutableMap<String, Double>) {
        when (result.threatType) {
            com.bp22intel.edgesentinel.domain.model.ThreatType.FAKE_BTS -> {
                // Fake BTS maps to: a5 (unknown LAC), a4 (missing neighbors), f1 (fingerprint)
                if (result.details.keys.any { it.startsWith("unknown_cid") }) {
                    indicators["a5"] = maxOf(indicators["a5"] ?: 0.0, 1.5)
                }
                if (result.details.containsKey("missing_neighbors")) {
                    indicators["a4"] = maxOf(indicators["a4"] ?: 0.0, 1.0)
                }
                if (result.details.keys.any { it.startsWith("strong_signal") }) {
                    indicators["f1"] = maxOf(indicators["f1"] ?: 0.0, 2.0)
                }
            }
            com.bp22intel.edgesentinel.domain.model.ThreatType.NETWORK_DOWNGRADE -> {
                // Network downgrade maps to: k1 (cipher mode downgrade)
                indicators["k1"] = maxOf(indicators["k1"] ?: 0.0, result.score)
            }
            com.bp22intel.edgesentinel.domain.model.ThreatType.TRACKING_PATTERN -> {
                // Tracking maps to: a1 (unusual LAC change), a2 (unusual reselection),
                // t3 (LAC change pattern), t4 (short cell duration)
                if (result.details.containsKey("unknown_lac")) {
                    indicators["a1"] = maxOf(indicators["a1"] ?: 0.0, 1.5)
                }
                if (result.details.containsKey("rapid_reselection")) {
                    indicators["a2"] = maxOf(indicators["a2"] ?: 0.0, 1.0)
                }
                if (result.details.containsKey("lac_oscillation")) {
                    indicators["t3"] = maxOf(indicators["t3"] ?: 0.0, 1.0)
                }
                if (result.details.containsKey("short_duration")) {
                    indicators["t4"] = maxOf(indicators["t4"] ?: 0.0, 1.0)
                }
            }
            com.bp22intel.edgesentinel.domain.model.ThreatType.SILENT_SMS -> {
                // Silent SMS maps to: t1 (TMSI oddity — closest available coefficient)
                indicators["t1"] = maxOf(indicators["t1"] ?: 0.0, 1.0)
            }
            com.bp22intel.edgesentinel.domain.model.ThreatType.CIPHER_ANOMALY -> {
                // Cipher anomaly maps to: k1, k2
                indicators["k1"] = maxOf(indicators["k1"] ?: 0.0, result.score)
                indicators["k2"] = maxOf(indicators["k2"] ?: 0.0, 1.0)
            }
            com.bp22intel.edgesentinel.domain.model.ThreatType.SIGNAL_ANOMALY -> {
                // Signal anomaly maps to: f1 (fingerprint)
                indicators["f1"] = maxOf(indicators["f1"] ?: 0.0, result.score)
            }
        }
    }
}
