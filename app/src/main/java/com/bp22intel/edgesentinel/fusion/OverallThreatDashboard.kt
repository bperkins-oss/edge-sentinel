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

package com.bp22intel.edgesentinel.fusion

import com.bp22intel.edgesentinel.domain.model.FusedThreatAssessment
import com.bp22intel.edgesentinel.domain.model.FusedThreatLevel
import com.bp22intel.edgesentinel.domain.model.SensorCategory
import com.bp22intel.edgesentinel.domain.model.SensorCategoryScore
import com.bp22intel.edgesentinel.domain.model.ThreatTrend
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class DashboardPosture(
    val level: FusedThreatLevel,
    val levelLabel: String,
    val categoryBreakdown: List<SensorCategoryScore>,
    val trend: ThreatTrend,
    val trendDescription: String,
    val timeSinceLastDetection: String,
    val activeThreatCount: Int,
    val briefSummary: String
)

@Singleton
class OverallThreatDashboard @Inject constructor(
    private val fusionEngine: SensorFusionEngine,
    private val threatNarrator: ThreatNarrator
) {
    val assessmentFlow: StateFlow<FusedThreatAssessment>
        get() = fusionEngine.currentAssessment

    fun computePosture(assessment: FusedThreatAssessment): DashboardPosture {
        val trendDescription = when (assessment.trend) {
            ThreatTrend.IMPROVING -> "Threat level decreasing over the last hour"
            ThreatTrend.STABLE -> "Threat level stable"
            ThreatTrend.WORSENING -> "Threat level increasing over the last hour"
        }

        val timeSinceStr = assessment.timeSinceLastSignificantDetection?.let {
            formatDuration(it)
        } ?: "No recent detections"

        val briefSummary = threatNarrator.generateBriefSummary(
            matchedRules = emptyList(),
            signals = assessment.contributingSignals,
            overallLevel = assessment.overallLevel
        )

        return DashboardPosture(
            level = assessment.overallLevel,
            levelLabel = assessment.overallLevel.label,
            categoryBreakdown = assessment.categoryScores,
            trend = assessment.trend,
            trendDescription = trendDescription,
            timeSinceLastDetection = timeSinceStr,
            activeThreatCount = assessment.activeThreatCount,
            briefSummary = briefSummary
        )
    }

    fun getCategoryStatus(
        category: SensorCategory,
        assessment: FusedThreatAssessment
    ): SensorCategoryScore {
        return assessment.categoryScores.find { it.category == category }
            ?: SensorCategoryScore(category, 0.0, 0, null)
    }

    private fun formatDuration(millis: Long): String {
        val seconds = millis / 1000
        return when {
            seconds < 60 -> "${seconds}s ago"
            seconds < 3600 -> "${seconds / 60}m ago"
            seconds < 86400 -> "${seconds / 3600}h ago"
            else -> "${seconds / 86400}d ago"
        }
    }
}
