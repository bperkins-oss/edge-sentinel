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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.bp22intel.edgesentinel.ui.components.CellInfoCardWithVerification
import com.bp22intel.edgesentinel.detection.tower.TowerVerifier
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
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Shield
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalHapticFeedback
import com.bp22intel.edgesentinel.ui.components.EmptyStateCard
import com.bp22intel.edgesentinel.ui.theme.StatusClear
import com.bp22intel.edgesentinel.ui.theme.StatusCritical
import com.bp22intel.edgesentinel.ui.theme.StatusDangerous
import com.bp22intel.edgesentinel.ui.theme.StatusElevated
import com.bp22intel.edgesentinel.ui.theme.Surface
import com.bp22intel.edgesentinel.ui.theme.SurfaceVariant
import com.bp22intel.edgesentinel.ui.theme.TextPrimary
import com.bp22intel.edgesentinel.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
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
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    // Haptic feedback on threat level changes
    val hapticFeedback = LocalHapticFeedback.current
    LaunchedEffect(posture.level) {
        if (posture.level != com.bp22intel.edgesentinel.domain.model.FusedThreatLevel.CLEAR) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(
        containerColor = BackgroundPrimary,
        floatingActionButton = { }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.forceScan() }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Version ID
            item {
                Text(
                    text = "Edge Sentinel v${com.bp22intel.edgesentinel.BuildConfig.VERSION_NAME} (Build ${com.bp22intel.edgesentinel.BuildConfig.VERSION_CODE})",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            // Unified fused threat indicator
            item {
                FusedThreatHeader(posture = posture)
            }

            // AI Situation Analysis — the headline insight
            item {
                SituationAnalysisCard(
                    posture = posture,
                    alertCount = recentAlerts.size,
                    fusedNarrative = if (fusedAssessment.contributingSignals.isNotEmpty())
                        fusedAssessment.narrative else null
                )
            }

            // Active Alerts — front and center
            if (recentAlerts.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Active Alerts (${recentAlerts.size})",
                        actionText = "View All",
                        onActionClick = { onNavigate("alerts") }
                    )
                }

                items(recentAlerts.take(5), key = { it.id }) { alert ->
                    AlertCard(
                        alert = alert,
                        onClick = { onAlertClick(alert) }
                    )
                }

                if (recentAlerts.size > 5) {
                    item {
                        TextButton(
                            onClick = { onNavigate("alerts") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("View all ${recentAlerts.size} alerts", color = AccentBlue)
                        }
                    }
                }
            }

            // Detection Layers — the 5 sensors
            item {
                SectionHeader(
                    title = "Detection Layers",
                    actionText = null,
                    onActionClick = {}
                )
            }

            item {
                DetectionLayerCards(
                    categoryScores = posture.categoryBreakdown,
                    onLayerClick = { category ->
                        val route = when (category) {
                            SensorCategory.CELLULAR -> "cell_info"
                            SensorCategory.WIFI -> "wifi"
                            SensorCategory.BLUETOOTH -> "bluetooth"
                            SensorCategory.NETWORK -> "network"
                            SensorCategory.BASELINE -> "baseline"
                        }
                        onNavigate(route)
                    }
                )
            }

            // Threat Map — full-width prominent card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate("threat_map") },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0A1628)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    AccentBlue.copy(alpha = 0.15f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Radar,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "THREAT RADAR",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AccentBlue,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Tactical map of detected threats around you",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.NavigateNext,
                            contentDescription = "Open",
                            tint = TextSecondary
                        )
                    }
                }
            }

            // Mesh Network
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate("mesh") },
                    colors = CardDefaults.cardColors(
                        containerColor = Surface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.People,
                            contentDescription = null,
                            tint = Color(0xFF06B6D4),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mesh Network",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Peer-to-peer BLE alerts",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.NavigateNext,
                            contentDescription = "Open",
                            tint = TextSecondary
                        )
                    }
                }
            }

            // Force Scan button
            item {
                Button(
                    onClick = { viewModel.forceScan() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        contentColor = BackgroundPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Force Scan", style = MaterialTheme.typography.labelLarge)
                }
            }

            // Monitoring status
            item {
                MonitoringStatusBar(
                    isActive = isMonitoring,
                    startTime = monitoringStartTime
                )
            }

            // Connected tower information
            item {
                SectionHeader(
                    title = "Connected Tower",
                    actionText = null,
                    onActionClick = {}
                )
            }

            item {
                val cell = currentCell
                if (cell != null) {
                    CellInfoCardWithVerification(cellTower = cell)
                } else {
                    EmptyStateCard(
                        icon = Icons.Filled.CellTower,
                        title = "No Cell Data",
                        subtitle = "Waiting for cell tower information"
                    )
                }
            }

            // Bottom spacer
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
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
private fun SensorCategoryBreakdown(
    categoryScores: List<SensorCategoryScore>,
    onCategoryClick: (SensorCategory) -> Unit = {}
) {
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
                SensorMiniIndicator(
                    score = score,
                    onClick = { onCategoryClick(score.category) }
                )
            }
        }
    }
}

@Composable
private fun SensorMiniIndicator(score: SensorCategoryScore, onClick: () -> Unit = {}) {
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
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(indicatorColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = score.category.icon,
                contentDescription = score.category.label,
                tint = indicatorColor,
                modifier = Modifier.size(18.dp)
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
private fun SituationAnalysisCard(
    posture: DashboardPosture,
    alertCount: Int,
    fusedNarrative: String?
) {
    val color = fusedLevelColor(posture.level)
    val (headline, detail, recommendation) = when {
        alertCount == 0 && posture.level == FusedThreatLevel.CLEAR -> Triple(
            "All Clear",
            "No threats detected across any sensor. Your cellular, WiFi, Bluetooth, and network connections appear normal for this location.",
            "No action needed. Monitoring continues in the background."
        )
        posture.level == FusedThreatLevel.ELEVATED -> Triple(
            "Elevated Activity Detected",
            fusedNarrative ?: "Multiple sensors are showing unusual activity. This could indicate surveillance equipment nearby or unusual network behavior worth monitoring.",
            "Avoid sensitive calls and texts until the situation resolves. Consider moving to a different location."
        )
        posture.level == FusedThreatLevel.DANGEROUS -> Triple(
            "High Threat Detected",
            fusedNarrative ?: "Significant anomalies detected across multiple sensors. The pattern is consistent with active interception or surveillance equipment in your vicinity.",
            "Do not make sensitive calls. Move to a different location. If this persists, the threat is likely following you."
        )
        posture.level == FusedThreatLevel.CRITICAL -> Triple(
            "Critical — Active Threat",
            fusedNarrative ?: "Multiple strong indicators of active surveillance or interception. Your communications may be compromised.",
            "Stop all sensitive communications immediately. Power off your device if possible. Move to a secure location."
        )
        alertCount > 0 -> Triple(
            "$alertCount Alert${if (alertCount != 1) "s" else ""} Active",
            fusedNarrative ?: "Potential threats have been detected. Review the alerts below for details.",
            "Review each alert and follow the recommended actions."
        )
        else -> Triple(
            "Monitoring Active",
            "Edge Sentinel is scanning all five detection layers. No significant activity detected.",
            "No action needed."
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "AI THREAT ANALYSIS",
                        style = MaterialTheme.typography.titleSmall,
                        color = color,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = headline,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight
            )

            // Recommendation
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Surface
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = recommendation,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun DetectionLayerCards(
    categoryScores: List<SensorCategoryScore>,
    onLayerClick: (SensorCategory) -> Unit = {}
) {
    val layerDescriptions = mapOf(
        SensorCategory.CELLULAR to "IMSI catcher, tower changes, silent SMS",
        SensorCategory.WIFI to "Evil twin, rogue AP, deauth attacks",
        SensorCategory.BLUETOOTH to "AirTag, SmartTag, Tile tracking",
        SensorCategory.NETWORK to "DNS, TLS, VPN integrity",
        SensorCategory.BASELINE to "RF environment anomaly detection"
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        categoryScores.forEach { score ->
            val statusColor = when {
                score.score > 0.7 -> StatusDangerous
                score.score > 0.3 -> StatusElevated
                score.activeThreatCount > 0 -> StatusElevated
                else -> StatusClear
            }
            val statusText = when {
                score.activeThreatCount > 0 -> "${score.activeThreatCount} alert${if (score.activeThreatCount != 1) "s" else ""}"
                else -> "Clear"
            }

            Card(
                onClick = { onLayerClick(score.category) },
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = score.category.icon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = score.category.label,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = layerDescriptions[score.category] ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }

                    // Status badge
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
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

@Composable
private fun MeshNetworkCard(onNavigate: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate() },
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mesh icon with cyan accent
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF06B6D4).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = "Mesh Network",
                    tint = Color(0xFF06B6D4),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mesh Network",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = "Share alerts with nearby devices",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            // Status indicator - you could enhance this to show real mesh status
            Text(
                text = "Tap to configure",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF06B6D4)
            )
        }
    }
}
