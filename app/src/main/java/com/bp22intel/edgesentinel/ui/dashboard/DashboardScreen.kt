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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.ui.components.AlertCard
import com.bp22intel.edgesentinel.ui.components.CellInfoCard
import com.bp22intel.edgesentinel.ui.components.MonitoringStatusBar
import com.bp22intel.edgesentinel.ui.components.SectionHeader
import com.bp22intel.edgesentinel.ui.components.ThreatIndicator
import com.bp22intel.edgesentinel.ui.theme.AccentBlue
import com.bp22intel.edgesentinel.ui.theme.BackgroundPrimary
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
            // Threat indicator
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ThreatIndicator(
                        threatLevel = threatLevel,
                        size = 200.dp
                    )
                }
            }

            // Monitoring status bar
            item {
                MonitoringStatusBar(
                    isActive = isMonitoring,
                    startTime = monitoringStartTime
                )
            }

            // Current cell info
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
