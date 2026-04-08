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

package com.bp22intel.edgesentinel.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.domain.model.FusedThreatLevel
import com.bp22intel.edgesentinel.domain.model.SensorCategory
import com.bp22intel.edgesentinel.domain.model.SensorCategoryScore
import com.bp22intel.edgesentinel.domain.model.ThreatTrend
import com.bp22intel.edgesentinel.fusion.DashboardPosture
import com.bp22intel.edgesentinel.ui.components.AlertCard
import com.bp22intel.edgesentinel.ui.components.CellInfoCard
import com.bp22intel.edgesentinel.ui.components.MonitoringStatusBar
import com.bp22intel.edgesentinel.ui.components.SectionHeader
import com.bp22intel.edgesentinel.ui.components.ThreatIndicator
import com.bp22intel.edgesentinel.ui.theme.AccentBlue
import com.bp22intel.edgesentinel.ui.theme.BackgroundPrimary
import com.bp22intel.edgesentinel.ui.theme.SensorBaseline
import com.bp22intel.edgesentinel.ui.theme.SensorBluetooth
import com.bp22intel.edgesentinel.ui.theme.SensorCellular
import com.bp22intel.edgesentinel.ui.theme.SensorNetwork
import com.bp22intel.edgesentinel.ui.theme.SensorWifi
import com.bp22intel.edgesentinel.ui.theme.StatusClear
import com.bp22intel.edgesentinel.ui.theme.StatusCritical
import com.bp22intel.edgesentinel.ui.theme.StatusDangerous
import com.bp22intel.edgesentinel.ui.theme.StatusElevated
import com.bp22intel.edgesentinel.ui.theme.Surface
import com.bp22intel.edgesentinel.ui.theme.SurfaceVariant
import com.bp22intel.edgesentinel.ui.theme.TextPrimary
import com.bp22intel.edgesentinel.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onAlertClick: (Alert) -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val threatLevel by viewModel.currentThreatLevel.collectAsState()
    val currentCell by viewModel.currentCell.collectAsState()
    val recentAlerts by viewModel.recentAlerts.collectAsState()
    val isMonitoring by viewModel.isMonitoring.collectAsState()
    val monitoringStartTime by viewModel.monitoringStartTime.collectAsState()
    val posture by viewModel.dashboardPosture.collectAsState()
    val fusedAssessment by viewModel.fusedAssessment.collectAsState()

    Scaffold(
        containerColor = BackgroundPrimary,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.forceScan() },
                containerColor = AccentBlue,
                contentColor = BackgroundPrimary
            ) {
                Icon(
                    imageVector = Icons.Filled.Radar,
                    contentDescription = "Force Scan"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Unified fused threat indicator
            item {
                FusedThreatHeader(posture = posture)
            }

            // Sensor category breakdown — 5 mini indicators
            item {
                SensorCategoryBreakdown(categoryScores = posture.categoryBreakdown)
            }

            // Trend and active threats summary
            item {
                ThreatSummaryBar(posture = posture)
            }

            // Active fused narrative
            if (fusedAssessment.contributingSignals.isNotEmpty()) {
                item {
                    FusedNarrativeCard(narrative = fusedAssessment.narrative)
                }
            }

            // Monitoring status bar
            item {
                MonitoringStatusBar(
                    isActive = isMonitoring,
                    startTime = monitoringStartTime
                )
            }

            // Cell-specific detail (existing)
            item {
                SectionHeader(
                    title = "Cell Tower Detail",
                    actionText = null,
                    onActionClick = {}
                )
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ThreatIndicator(
                        threatLevel = threatLevel,
                        size = 140.dp
                    )
                }
            }

            item {
                val cell = currentCell
                if (cell != null) {
                    CellInfoCard(cellTower = cell)
                } else {
                    Text(
                        text = "No cell information available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Recent alerts section header
            item {
                SectionHeader(
                    title = "Recent Alerts",
                    actionText = "View All",
                    onActionClick = { onNavigate("alerts") }
                )
            }

            // Alert list
            if (recentAlerts.isEmpty()) {
                item {
                    Text(
                        text = "No alerts yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            } else {
                items(recentAlerts, key = { it.id }) { alert ->
                    AlertCard(
                        alert = alert,
                        onClick = { onAlertClick(alert) }
                    )
                }
            }

            // Bottom spacer for FAB clearance
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
private fun FusedThreatHeader(posture: DashboardPosture) {
    val color = fusedLevelColor(posture.level)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Large threat level circle
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = posture.levelLabel.uppercase(),
                    color = color,
                    fontSize = if (posture.levelLabel.length > 6) 14.sp else 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Fused Threat Posture",
                style = MaterialTheme.typography.titleSmall,
                color = TextSecondary
            )

            if (posture.activeThreatCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${posture.activeThreatCount} active signal${if (posture.activeThreatCount != 1) "s" else ""} across sensors",
                    style = MaterialTheme.typography.bodySmall,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun SensorCategoryBreakdown(categoryScores: List<SensorCategoryScore>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            categoryScores.forEach { score ->
                SensorMiniIndicator(score = score)
            }
        }
    }
}

@Composable
private fun SensorMiniIndicator(score: SensorCategoryScore) {
    val categoryColor = when (score.category) {
        SensorCategory.CELLULAR -> SensorCellular
        SensorCategory.WIFI -> SensorWifi
        SensorCategory.BLUETOOTH -> SensorBluetooth
        SensorCategory.NETWORK -> SensorNetwork
        SensorCategory.BASELINE -> SensorBaseline
    }

    val indicatorColor = when {
        score.score > 0.7 -> StatusDangerous
        score.score > 0.3 -> StatusElevated
        score.activeThreatCount > 0 -> StatusElevated
        else -> StatusClear
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(indicatorColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = score.category.icon,
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = score.category.label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontSize = 10.sp
        )
        if (score.activeThreatCount > 0) {
            Text(
                text = "${score.activeThreatCount}",
                style = MaterialTheme.typography.labelSmall,
                color = indicatorColor,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun ThreatSummaryBar(posture: DashboardPosture) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trend
            Row(verticalAlignment = Alignment.CenterVertically) {
                val trendColor = when (posture.trend) {
                    ThreatTrend.IMPROVING -> StatusClear
                    ThreatTrend.STABLE -> TextSecondary
                    ThreatTrend.WORSENING -> StatusDangerous
                }
                Text(
                    text = posture.trend.arrow,
                    fontSize = 18.sp,
                    color = trendColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = posture.trend.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = trendColor
                )
            }

            // Last detection
            Text(
                text = posture.timeSinceLastDetection,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun FusedNarrativeCard(narrative: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Threat Analysis",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = narrative,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp,
                maxLines = 12,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun fusedLevelColor(level: FusedThreatLevel): Color {
    return when (level) {
        FusedThreatLevel.CLEAR -> StatusClear
        FusedThreatLevel.ELEVATED -> StatusElevated
        FusedThreatLevel.DANGEROUS -> StatusDangerous
        FusedThreatLevel.CRITICAL -> StatusCritical
    }
}
