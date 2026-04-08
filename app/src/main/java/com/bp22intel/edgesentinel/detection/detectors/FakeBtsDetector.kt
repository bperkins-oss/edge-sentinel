/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024-2026 BP22 Intel. All Rights Reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use of this software, in whole or in
 * part, is strictly prohibited without prior written permission from
 * BP22 Intel.
 */

package com.bp22intel.edgesentinel.detection.detectors

import com.bp22intel.edgesentinel.domain.model.CellTower
import com.bp22intel.edgesentinel.domain.model.Confidence
import com.bp22intel.edgesentinel.domain.model.DetectionResult
import com.bp22intel.edgesentinel.domain.model.ThreatType
import javax.inject.Inject

/**
 * Detects potential fake base transceiver stations (IMSI catchers / Stingrays).
 *
 * Checks performed:
 * - Abnormally strong signal strength (> -50 dBm)
 * - Cell ID not present in historical observation database
 * - Missing neighbor cells (real towers typically have neighbors)
 * - Unusual MCC/MNC combinations that don't match known carriers
 */
class FakeBtsDetector @Inject constructor() : ThreatDetector {

    override val type: ThreatType = ThreatType.FAKE_BTS

    companion object {
        /** Signal stronger than this is suspicious for a cell tower. */
        private const val STRONG_SIGNAL_THRESHOLD = -50

        /**
         * Known US carrier MCC/MNC pairs. Cells outside this set in the US
         * are flagged as potentially suspicious.
         */
        private val KNOWN_US_CARRIERS = setOf(
            310 to 260, // T-Mobile
            310 to 410, // AT&T
            311 to 480, // Verizon
            312 to 530, // Sprint/T-Mobile
            310 to 120, // Sprint
            311 to 490, // T-Mobile
            310 to 150, // AT&T
            310 to 170, // AT&T
            310 to 380, // AT&T
            311 to 280, // AT&T
            312 to 280, // Visible (Verizon MVNO)
        )
    }

    override suspend fun analyze(
        cells: List<CellTower>,
        history: List<CellTower>
    ): DetectionResult? {
        if (cells.isEmpty()) return null

        val knownCids = history.map { it.cid }.toSet()
        val indicators = mutableMapOf<String, String>()
        var score = 0.0

        for (cell in cells) {
            // Check 1: Abnormally strong signal
            if (cell.signalStrength > STRONG_SIGNAL_THRESHOLD) {
                val contribution = 1.5
                score += contribution
                indicators["strong_signal_${cell.cid}"] =
                    "Signal ${cell.signalStrength} dBm exceeds threshold ($STRONG_SIGNAL_THRESHOLD dBm)"
            }

            // Check 2: Unknown cell ID (not in history)
            if (cell.cid !in knownCids && history.isNotEmpty()) {
                val contribution = 1.0
                score += contribution
                indicators["unknown_cid_${cell.cid}"] =
                    "Cell ID ${cell.cid} not found in observation history (${history.size} known cells)"
            }

            // Check 3: Unusual MCC/MNC (for US-based devices)
            if (cell.mcc in 310..316) {
                val pair = cell.mcc to cell.mnc
                if (pair !in KNOWN_US_CARRIERS) {
                    val contribution = 0.5
                    score += contribution
                    indicators["unusual_carrier_${cell.cid}"] =
                        "MCC/MNC ${cell.mcc}/${cell.mnc} not in known US carrier list"
                }
            }
        }

        // Check 4: Missing neighbor cells — if we see only 1 cell where we usually see many
        if (cells.size == 1 && history.isNotEmpty()) {
            val avgHistoricalNeighbors = history.groupBy { it.lacTac }
                .values
                .map { it.size }
                .average()

            if (avgHistoricalNeighbors > 2.0) {
                val contribution = 1.0
                score += contribution
                indicators["missing_neighbors"] =
                    "Only 1 cell visible; historical average is %.1f per LAC/TAC".format(
                        avgHistoricalNeighbors
                    )
            }
        }

        if (score <= 0.0) return null

        val confidence = when {
            score >= 3.0 -> Confidence.HIGH
            score >= 1.5 -> Confidence.MEDIUM
            else -> Confidence.LOW
        }

        return DetectionResult(
            threatType = type,
            score = score,
            confidence = confidence,
            summary = buildSummary(indicators),
            details = indicators
        )
    }

    private fun buildSummary(indicators: Map<String, String>): String {
        val count = indicators.size
        return when {
            count >= 3 -> "Multiple fake BTS indicators detected: strong signal, unknown cell, and carrier anomalies"
            indicators.keys.any { it.startsWith("strong_signal") } ->
                "Abnormally strong cell signal detected — possible nearby IMSI catcher"
            indicators.keys.any { it.startsWith("unknown_cid") } ->
                "Unknown cell tower detected that is not in observation history"
            else -> "Potential fake base station indicators detected ($count anomalies)"
        }
    }
}
