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

package com.bp22intel.edgesentinel.detection.scoring

import com.bp22intel.edgesentinel.domain.model.DetectionSensitivity
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Threat scoring engine ported from SnoopSnitch's ImsiCatcher.java coefficient model.
 *
 * Each coefficient represents a measurable indicator of IMSI catcher activity.
 * Coefficients prefixed with k/c/t are stubs requiring root or DIAG access.
 */
@Singleton
class ThreatScorer @Inject constructor() {

    data class ScoringResult(
        /** Unusual LAC change (0.0-2.0) */
        val a1: Double = 0.0,
        /** Unusual cell reselection (0.0-1.0) */
        val a2: Double = 0.0,
        /** Many neighbor cells missing (0.0-1.0) */
        val a4: Double = 0.0,
        /** LAC not seen before (0.0-1.5) */
        val a5: Double = 0.0,
        /** Cipher mode downgrade — stub, root required (0.0-3.0) */
        val k1: Double = 0.0,
        /** Integrity mode missing — stub (0.0-1.0) */
        val k2: Double = 0.0,
        /** Cipher criterion 1 — stub (0.0-1.0) */
        val c1: Double = 0.0,
        /** Cipher criterion 2 — stub (0.0-1.0) */
        val c2: Double = 0.0,
        /** Cipher criterion 3 — stub (0.0-1.0) */
        val c3: Double = 0.0,
        /** Cipher criterion 4 — stub (0.0-1.0) */
        val c4: Double = 0.0,
        /** Cipher criterion 5 — stub (0.0-1.0) */
        val c5: Double = 0.0,
        /** TMSI reallocation oddity — stub (0.0-1.0) */
        val t1: Double = 0.0,
        /** LAC change pattern (0.0-1.0) */
        val t3: Double = 0.0,
        /** Short cell duration (0.0-1.0) */
        val t4: Double = 0.0,
        /** Reject cause (0.0-1.5) */
        val r1: Double = 0.0,
        /** LU reject (0.0-1.0) */
        val r2: Double = 0.0,
        /** Known fingerprint match (0.0-3.0) */
        val f1: Double = 0.0,
        /** Total composite score */
        val total: Double = 0.0,
        /** Mapped threat level based on score and sensitivity */
        val threatLevel: ThreatLevel = ThreatLevel.CLEAR
    )

    companion object {
        // Maximum values for each coefficient (used for clamping)
        private val MAX_VALUES = mapOf(
            "a1" to 2.0, "a2" to 1.0, "a4" to 1.0, "a5" to 1.5,
            "k1" to 3.0, "k2" to 1.0,
            "c1" to 1.0, "c2" to 1.0, "c3" to 1.0, "c4" to 1.0, "c5" to 1.0,
            "t1" to 1.0, "t3" to 1.0, "t4" to 1.0,
            "r1" to 1.5, "r2" to 1.0,
            "f1" to 3.0
        )

        private const val SUSPICIOUS_THRESHOLD = 3.0
        private const val THREAT_THRESHOLD = 6.0
    }

    /**
     * Calculate a composite threat score from a map of indicator values.
     *
     * @param indicators Map of coefficient names (e.g. "a1", "k1") to raw values.
     *                   Values are clamped to [0, max] for each coefficient.
     * @param sensitivity Detection sensitivity that adjusts thresholds.
     * @return A [ScoringResult] with all individual coefficients, total, and threat level.
     */
    fun calculateScore(
        indicators: Map<String, Double>,
        sensitivity: DetectionSensitivity = DetectionSensitivity.MEDIUM
    ): ScoringResult {
        fun clamp(key: String): Double {
            val raw = indicators[key] ?: 0.0
            val max = MAX_VALUES[key] ?: 0.0
            return raw.coerceIn(0.0, max)
        }

        val a1 = clamp("a1")
        val a2 = clamp("a2")
        val a4 = clamp("a4")
        val a5 = clamp("a5")
        val k1 = clamp("k1")
        val k2 = clamp("k2")
        val c1 = clamp("c1")
        val c2 = clamp("c2")
        val c3 = clamp("c3")
        val c4 = clamp("c4")
        val c5 = clamp("c5")
        val t1 = clamp("t1")
        val t3 = clamp("t3")
        val t4 = clamp("t4")
        val r1 = clamp("r1")
        val r2 = clamp("r2")
        val f1 = clamp("f1")

        val total = a1 + a2 + a4 + a5 +
            k1 + k2 +
            c1 + c2 + c3 + c4 + c5 +
            t1 + t3 + t4 +
            r1 + r2 +
            f1

        val threatLevel = mapToThreatLevel(total, sensitivity)

        return ScoringResult(
            a1 = a1, a2 = a2, a4 = a4, a5 = a5,
            k1 = k1, k2 = k2,
            c1 = c1, c2 = c2, c3 = c3, c4 = c4, c5 = c5,
            t1 = t1, t3 = t3, t4 = t4,
            r1 = r1, r2 = r2,
            f1 = f1,
            total = total,
            threatLevel = threatLevel
        )
    }

    /**
     * Map a raw score to a [ThreatLevel] adjusted by sensitivity.
     *
     * LOW sensitivity multiplies thresholds by 1.5 (harder to trigger).
     * HIGH sensitivity multiplies thresholds by 0.7 (easier to trigger).
     */
    private fun mapToThreatLevel(score: Double, sensitivity: DetectionSensitivity): ThreatLevel {
        val multiplier = sensitivity.thresholdMultiplier
        val suspiciousThreshold = SUSPICIOUS_THRESHOLD * multiplier
        val threatThreshold = THREAT_THRESHOLD * multiplier

        return when {
            score >= threatThreshold -> ThreatLevel.THREAT
            score >= suspiciousThreshold -> ThreatLevel.SUSPICIOUS
            else -> ThreatLevel.CLEAR
        }
    }
}
