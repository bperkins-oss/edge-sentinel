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

package com.bp22intel.edgesentinel.domain.model

enum class FusedThreatLevel(val label: String, val ordinal_rank: Int) {
    CLEAR("Clear", 0),
    ELEVATED("Elevated", 1),
    DANGEROUS("Dangerous", 2),
    CRITICAL("Critical", 3)
}

enum class ThreatTrend(val label: String, val arrow: String) {
    IMPROVING("Improving", "↓"),
    STABLE("Stable", "→"),
    WORSENING("Worsening", "↑")
}

data class ContributingSignal(
    val category: SensorCategory,
    val detectionType: String,
    val description: String,
    val score: Double,
    val timestamp: Long
)

data class SensorCategoryScore(
    val category: SensorCategory,
    val score: Double,
    val activeThreatCount: Int,
    val latestDetection: String?
)

data class FusedThreatAssessment(
    val overallLevel: FusedThreatLevel,
    val contributingSignals: List<ContributingSignal>,
    val confidence: Double,
    val narrative: String,
    val timestamp: Long,
    val trend: ThreatTrend,
    val categoryScores: List<SensorCategoryScore>,
    val activeThreatCount: Int,
    val timeSinceLastSignificantDetection: Long?
) {
    companion object {
        fun clear() = FusedThreatAssessment(
            overallLevel = FusedThreatLevel.CLEAR,
            contributingSignals = emptyList(),
            confidence = 1.0,
            narrative = "All sensors nominal. No threats detected.",
            timestamp = System.currentTimeMillis(),
            trend = ThreatTrend.STABLE,
            categoryScores = SensorCategory.entries.map {
                SensorCategoryScore(it, 0.0, 0, null)
            },
            activeThreatCount = 0,
            timeSinceLastSignificantDetection = null
        )
    }
}
