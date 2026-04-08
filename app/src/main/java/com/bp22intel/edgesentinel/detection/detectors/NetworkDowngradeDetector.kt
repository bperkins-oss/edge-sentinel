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
import com.bp22intel.edgesentinel.domain.model.NetworkType
import com.bp22intel.edgesentinel.domain.model.ThreatType
import javax.inject.Inject

/**
 * Detects suspicious network technology downgrades.
 *
 * IMSI catchers often force devices from 4G/5G down to 2G where encryption
 * is weaker or absent. This detector compares the current network type against
 * the device's recent history to identify forced downgrades.
 */
class NetworkDowngradeDetector @Inject constructor() : ThreatDetector {

    override val type: ThreatType = ThreatType.NETWORK_DOWNGRADE

    companion object {
        /** Ordered generation ranking. Higher index = newer/more secure. */
        private val GENERATION_RANK = mapOf(
            NetworkType.GSM to 0,
            NetworkType.CDMA to 0,
            NetworkType.WCDMA to 1,
            NetworkType.LTE to 2,
            NetworkType.NR to 3,
            NetworkType.UNKNOWN to -1
        )

        /** Time window to consider for historical baseline (15 minutes). */
        private const val HISTORY_WINDOW_MS = 15 * 60 * 1000L
    }

    override suspend fun analyze(
        cells: List<CellTower>,
        history: List<CellTower>
    ): DetectionResult? {
        if (cells.isEmpty() || history.isEmpty()) return null

        val now = System.currentTimeMillis()
        val recentHistory = history.filter { now - it.lastSeen < HISTORY_WINDOW_MS }
        if (recentHistory.isEmpty()) return null

        // Determine the predominant historical network type
        val historicalBest = recentHistory
            .groupBy { it.networkType }
            .maxByOrNull { (netType, towers) ->
                val rank = GENERATION_RANK[netType] ?: -1
                rank * 1000 + towers.size // Prefer higher rank, break ties by count
            }?.key ?: return null

        val historicalRank = GENERATION_RANK[historicalBest] ?: return null

        // Check current cells for downgrades
        val indicators = mutableMapOf<String, String>()
        var worstDowngrade = 0

        for (cell in cells) {
            val currentRank = GENERATION_RANK[cell.networkType] ?: continue
            if (currentRank < 0) continue // Skip UNKNOWN

            val downgradeSteps = historicalRank - currentRank
            if (downgradeSteps > 0) {
                worstDowngrade = maxOf(worstDowngrade, downgradeSteps)
                indicators["downgrade_${cell.cid}"] =
                    "${historicalBest.generation} -> ${cell.networkType.generation} " +
                        "(${downgradeSteps}-step downgrade on CID ${cell.cid})"
            }
        }

        if (worstDowngrade == 0) return null

        // Score based on severity: 2G fallback from 4G/5G is much more suspicious
        val score = when (worstDowngrade) {
            1 -> 1.5  // One step (e.g. 4G -> 3G) — mildly suspicious
            2 -> 3.0  // Two steps (e.g. 4G -> 2G) — very suspicious
            3 -> 4.5  // Three steps (e.g. 5G -> 2G) — highly suspicious
            else -> 2.0
        }

        // Check if this might be normal roaming (known carrier at lower generation)
        val isLikelyRoaming = cells.any { cell ->
            val cellRank = GENERATION_RANK[cell.networkType] ?: -1
            cellRank < historicalRank && cell.timesSeen > 5
        }

        val adjustedScore = if (isLikelyRoaming) score * 0.5 else score

        val confidence = when {
            worstDowngrade >= 2 && !isLikelyRoaming -> Confidence.HIGH
            worstDowngrade >= 2 || !isLikelyRoaming -> Confidence.MEDIUM
            else -> Confidence.LOW
        }

        return DetectionResult(
            threatType = type,
            score = adjustedScore,
            confidence = confidence,
            summary = buildSummary(historicalBest, worstDowngrade, isLikelyRoaming),
            details = indicators
        )
    }

    private fun buildSummary(
        baseline: NetworkType,
        steps: Int,
        likelyRoaming: Boolean
    ): String {
        val severity = when (steps) {
            1 -> "minor"
            2 -> "significant"
            else -> "severe"
        }
        val roamingNote = if (likelyRoaming) " (may be normal roaming)" else ""
        return "Detected $severity network downgrade from ${baseline.generation}$roamingNote"
    }
}
