/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024-2026 BP22 Intel. All Rights Reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use of this software, in whole or in
 * part, is strictly prohibited without prior written permission from
 * BP22 Intel.
 */

package com.bp22intel.edgesentinel.ui.alerts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.ui.components.AlertCard
import com.bp22intel.edgesentinel.ui.theme.AccentBlue
import com.bp22intel.edgesentinel.ui.theme.SensorCellular
import com.bp22intel.edgesentinel.ui.theme.SensorWifi
import com.bp22intel.edgesentinel.ui.theme.SensorBluetooth
import com.bp22intel.edgesentinel.ui.theme.SensorNetwork
import com.bp22intel.edgesentinel.ui.theme.SensorBaseline
import com.bp22intel.edgesentinel.ui.theme.Surface
import com.bp22intel.edgesentinel.ui.theme.TextPrimary
import com.bp22intel.edgesentinel.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertListScreen(
    viewModel: AlertsViewModel = hiltViewModel(),
    onAlertClick: (Alert) -> Unit = {}
) {
    val alerts by viewModel.alerts.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Severity filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AlertFilter.entries.forEach { filter ->
                val label = when (filter) {
                    AlertFilter.ALL -> "All"
                    AlertFilter.SUSPICIOUS -> "Suspicious"
                    AlertFilter.THREAT -> "Threat"
                }
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { viewModel.setFilter(filter) },
                    label = {
                        Text(
                            text = label,
                            color = if (selectedFilter == filter) TextPrimary else TextSecondary
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentBlue.copy(alpha = 0.2f),
                        containerColor = Surface
                    )
                )
            }
        }

        // Category filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryFilter.entries.forEach { filter ->
                val label = when (filter) {
                    CategoryFilter.ALL -> "All"
                    CategoryFilter.CELLULAR -> "Cellular"
                    CategoryFilter.WIFI -> "WiFi"
                    CategoryFilter.BLUETOOTH -> "Bluetooth"
                    CategoryFilter.NETWORK -> "Network"
                    CategoryFilter.BASELINE -> "Baseline"
                }
                val selectedColor = when (filter) {
                    CategoryFilter.ALL -> AccentBlue
                    CategoryFilter.CELLULAR -> SensorCellular
                    CategoryFilter.WIFI -> SensorWifi
                    CategoryFilter.BLUETOOTH -> SensorBluetooth
                    CategoryFilter.NETWORK -> SensorNetwork
                    CategoryFilter.BASELINE -> SensorBaseline
                }
                FilterChip(
                    selected = selectedCategoryFilter == filter,
                    onClick = { viewModel.setCategoryFilter(filter) },
                    label = {
                        Text(
                            text = label,
                            color = if (selectedCategoryFilter == filter) TextPrimary else TextSecondary
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = selectedColor.copy(alpha = 0.2f),
                        containerColor = Surface
                    )
                )
            }
        }

        // Alert list
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() }
        ) {
            if (alerts.isEmpty()) {
                Text(
                    text = "No alerts match the selected filter",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 24.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(alerts, key = { it.id }) { alert ->
                        AlertCard(
                            alert = alert,
                            onClick = { onAlertClick(alert) }
                        )
                    }
                }
            }
        }
    }
}
