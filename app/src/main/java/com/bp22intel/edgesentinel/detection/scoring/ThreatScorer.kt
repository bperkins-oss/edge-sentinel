/*
 * Edge Sentinel — Multi-Vector Threat Scoring Engine
 * Copyright (C) 2024-2026 BP22 Intel. All Rights Reserved.
 * Proprietary and confidential.
 *
 * Clean-room implementation. Scoring model designed from published academic
 * research on cellular network threat detection:
 *
 *   - Hussain et al., NDSS 2025 — identity-exposing message characterization
 *   - Dabrowski et al., ACSAC 2014 — cell tower anomaly patterns
 *   - Li et al., SMDFbs 2023 — 5G RRC behavior rule specifications
 *   - Shaik et al., NDSS 2016 — LTE protocol-level downgrade attacks
 *
 * No third-party code.
 */

package com.bp22intel.edgesentinel.detection.scoring

import com.bp22intel.edgesentinel.domain.model.DetectionSensitivity
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Computes a composite threat score from six orthogonal detection categories.
 *
 * Each category produces a normalised sub-score in [0, 1].  The final score
 * is a weighted sum mapped to a [ThreatLevel] according to the user's
 * configured [DetectionSensitivity].
 *
 * ### Category overview
 *
 * | Category              | Weight | What it measures                                     |
 * |-----------------------|--------|------------------------------------------------------|
 * | signalAnomaly         |  0.20  | Unexpected RF characteristics (strength, uniformity)  |
 * | towerBehavior         |  0.20  | Cell-ID / LAC churn, missing neighbour lists          |
 * | protocolViolation     |  0.25  | Cipher downgrades, forced reselections, null ciphers  |
 * | networkIntegrity      |  0.15  | RAT downgrades, authentication failures               |
 * | temporalPattern       |  0.10  | Timing anomalies (rapid changes while stationary)     |
 * | environmentalContext  |  0.10  | Urban/rural and motion adjustments                    |
 */
@Singleton
class ThreatScorer @Inject constructor() {

    // -----------------------------------------------------------------
    // Public result type
    // -----------------------------------------------------------------

    /**
     * Detailed output of the scoring pipeline.
     *
     * @property signalAnomaly       Normalised signal-anomaly sub-score [0, 1].
     * @property towerBehavior       Normalised tower-behaviour sub-score [0, 1].
     * @property protocolViolation   Normalised protocol-violation sub-score [0, 1].
     * @property networkIntegrity    Normalised network-integrity sub-score [0, 1].
     * @property temporalPattern     Normalised temporal-pattern sub-score [0, 1].
     * @property environmentalContext Normalised environmental-context sub-score [0, 1].
     * @property total               Weighted composite score (0 – 10 scale).
     * @property threatLevel         Final mapped threat level.
     */
    data class ScoringResult(
        val signalAnomaly: Double = 0.0,
        val towerBehavior: Double = 0.0,
        val protocolViolation: Double = 0.0,
        val networkIntegrity: Double = 0.0,
        val temporalPattern: Double = 0.0,
        val environmentalContext: Double = 0.0,
        val total: Double = 0.0,
        val threatLevel: ThreatLevel = ThreatLevel.CLEAR
    )

    // -----------------------------------------------------------------
    // Category weights — must sum to 1.0
    // -----------------------------------------------------------------

    companion object {
        /** Unusual RF signal characteristics (strength jumps, uniform power). */
        private const val W_SIGNAL_ANOMALY       = 0.20
        /** Cell-ID / LAC / neighbour-list anomalies. */
        private const val W_TOWER_BEHAVIOR       = 0.20
        /** Cipher downgrades, forced reselections, null-cipher usage. */
        private const val W_PROTOCOL_VIOLATION   = 0.25
        /** RAT downgrades, authentication bypass indicators. */
        private const val W_NETWORK_INTEGRITY    = 0.15
        /** Rapid cell changes while stationary, timing irregularities. */
        private const val W_TEMPORAL_PATTERN     = 0.10
        /** Environmental adjustments (urban density, motion state). */
        private const val W_ENVIRONMENTAL        = 0.10

        /** Scale factor to map weighted [0,1] sum to a 0–10 threat scale. */
        private const val SCORE_SCALE = 10.0

        /** Default threshold: score ≥ this → SUSPICIOUS. */
        private const val SUSPICIOUS_THRESHOLD = 3.0
        /** Default threshold: score ≥ this → THREAT. */
        private const val THREAT_THRESHOLD     = 6.0

        // -- Indicator key constants (used by ThreatDetectionEngine) --

        const val KEY_SIGNAL_ANOMALY       = "signalAnomaly"
        const val KEY_TOWER_BEHAVIOR       = "towerBehavior"
        const val KEY_PROTOCOL_VIOLATION   = "protocolViolation"
        const val KEY_NETWORK_INTEGRITY    = "networkIntegrity"
        const val KEY_TEMPORAL_PATTERN     = "temporalPattern"
        const val KEY_ENVIRONMENTAL        = "environmentalContext"
    }

    // -----------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------

    /**
     * Calculate a composite threat score from a map of category indicators.
     *
     * @param indicators  Map of category keys (see [KEY_SIGNAL_ANOMALY] etc.)
     *                    to raw values.  Values are clamped to [0, 1].
     * @param sensitivity Detection sensitivity that shifts thresholds.
     * @return A [ScoringResult] with per-category scores, weighted total,
     *         and the resulting [ThreatLevel].
     */
    fun calculateScore(
        indicators: Map<String, Double>,
        sensitivity: DetectionSensitivity = DetectionSensitivity.MEDIUM
    ): ScoringResult {
        val signal   = clamp01(indicators[KEY_SIGNAL_ANOMALY])
        val tower    = clamp01(indicators[KEY_TOWER_BEHAVIOR])
        val protocol = clamp01(indicators[KEY_PROTOCOL_VIOLATION])
        val network  = clamp01(indicators[KEY_NETWORK_INTEGRITY])
        val temporal  = clamp01(indicators[KEY_TEMPORAL_PATTERN])
        val environ  = clamp01(indicators[KEY_ENVIRONMENTAL])

        val weighted = (signal   * W_SIGNAL_ANOMALY +
                        tower    * W_TOWER_BEHAVIOR +
                        protocol * W_PROTOCOL_VIOLATION +
                        network  * W_NETWORK_INTEGRITY +
                        temporal * W_TEMPORAL_PATTERN +
                        environ  * W_ENVIRONMENTAL) * SCORE_SCALE

        val level = resolveLevel(weighted, sensitivity)

        return ScoringResult(
            signalAnomaly       = signal,
            towerBehavior       = tower,
            protocolViolation   = protocol,
            networkIntegrity    = network,
            temporalPattern     = temporal,
            environmentalContext = environ,
            total               = weighted,
            threatLevel         = level
        )
    }

    // -----------------------------------------------------------------
    // Internals
    // -----------------------------------------------------------------

    /** Clamp a nullable value into [0.0, 1.0]. */
    private fun clamp01(value: Double?): Double =
        (value ?: 0.0).coerceIn(0.0, 1.0)

    /**
     * Map a composite score to a [ThreatLevel], adjusting thresholds by
     * the user's chosen [DetectionSensitivity].
     *
     * LOW  → multiplier > 1 → harder to trigger alerts.
     * HIGH → multiplier < 1 → easier to trigger alerts.
     */
    private fun resolveLevel(
        score: Double,
        sensitivity: DetectionSensitivity
    ): ThreatLevel {
        val m = sensitivity.thresholdMultiplier
        return when {
            score >= THREAT_THRESHOLD     * m -> ThreatLevel.THREAT
            score >= SUSPICIOUS_THRESHOLD * m -> ThreatLevel.SUSPICIOUS
            else                              -> ThreatLevel.CLEAR
        }
    }
}
