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

package com.bp22intel.edgesentinel.ui.wifi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bp22intel.edgesentinel.detection.wifi.ObservedAp
import com.bp22intel.edgesentinel.detection.wifi.ProbeLeakRisk
import com.bp22intel.edgesentinel.detection.wifi.SecurityType
import com.bp22intel.edgesentinel.detection.wifi.WifiDetectionResult
import com.bp22intel.edgesentinel.domain.model.Confidence
import com.bp22intel.edgesentinel.domain.model.WifiThreatType
import com.bp22intel.edgesentinel.ui.theme.BackgroundPrimary
import com.bp22intel.edgesentinel.ui.theme.TextPrimary

@Composable
fun WifiScreen(
    onBack: () -> Unit = {},
    viewModel: WifiViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WiFi Threats") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundPrimary,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        // WiFi connection status + security assessment
        item { ConnectionStatusCard(uiState) }

        // Environment health score
        item { EnvironmentHealthCard(uiState.healthScore, uiState.environmentAnalysis?.summary) }

        // Evil twin warnings with side-by-side comparison
        val evilTwinThreats = uiState.threats.filter { it.threatType == WifiThreatType.EVIL_TWIN }
        if (evilTwinThreats.isNotEmpty()) {
            item {
                Text(
                    "Evil Twin Warnings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF4444),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            items(evilTwinThreats, key = { "et_${it.details["ssid"]}" }) { threat ->
                EvilTwinCard(threat)
            }
        }

        // Other threat alerts
        val otherThreats = uiState.threats.filter { it.threatType != WifiThreatType.EVIL_TWIN }
        if (otherThreats.isNotEmpty()) {
            item {
                Text(
                    "Threat Alerts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            items(otherThreats, key = { "${it.threatType}_${it.details.hashCode()}" }) { threat ->
                ThreatAlertCard(threat)
            }
        }

        // Probe request privacy
        uiState.probeStatus?.let { status ->
            item { ProbePrivacyCard(status) }
        }

        // Nearby APs list
        if (uiState.accessPoints.isNotEmpty()) {
            item {
                Text(
                    "Nearby Access Points (${uiState.accessPoints.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            val sortedAps = uiState.accessPoints.sortedByDescending { it.signalStrength }
            items(sortedAps, key = { it.bssid }) { ap ->
                val isTrusted = viewModel.isNetworkTrusted(ap.bssid)
                AccessPointCard(
                    ap = ap,
                    threatLevel = if (isTrusted) ApThreatLevel.SAFE else viewModel.threatLevelForAp(ap),
                    isTrusted = isTrusted,
                    onToggleTrust = {
                        if (isTrusted) viewModel.untrustNetwork(ap.bssid)
                        else viewModel.trustNetwork(ap.bssid, ap.ssid)
                    }
                )
            }
        }

        // Bottom spacer for FAB clearance
        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
    }
}

@Composable
private fun ConnectionStatusCard(state: WifiUiState) {
    val statusColor = when {
        state.threats.any { it.confidence == Confidence.HIGH } -> Color(0xFFEF4444)
        state.threats.isNotEmpty() -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }
    val statusText = when {
        !state.isScanning -> "Not Scanning"
        state.threats.any { it.confidence == Confidence.HIGH } -> "Threats Detected"
        state.threats.isNotEmpty() -> "Suspicious Activity"
        else -> "Secure"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "WiFi Security",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )
                if (state.isScanning) {
                    Text(
                        "${state.accessPoints.size} APs visible · ${state.threats.size} alert(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (state.isScanning) statusColor else Color.Gray)
            )
        }
    }
}

@Composable
private fun EnvironmentHealthCard(healthScore: Int, summary: String?) {
    val healthColor = when {
        healthScore >= 80 -> Color(0xFF10B981)
        healthScore >= 50 -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Environment Health",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "$healthScore / 100",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = healthColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { healthScore / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = healthColor,
                trackColor = healthColor.copy(alpha = 0.2f),
            )
            summary?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EvilTwinCard(threat: WifiDetectionResult) {
    val severityColor = when (threat.confidence) {
        Confidence.HIGH -> Color(0xFFEF4444)
        Confidence.MEDIUM -> Color(0xFFF59E0B)
        Confidence.LOW -> Color(0xFF10B981)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = severityColor.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = severityColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "SSID: ${threat.details["ssid"] ?: "Unknown"}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                ConfidenceBadge(threat.confidence, severityColor)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                threat.summary,
                style = MaterialTheme.typography.bodySmall
            )

            // Side-by-side AP comparison
            if (threat.involvedAps.size >= 2) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    threat.involvedAps.take(2).forEach { ap ->
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    ap.bssid,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                ApDetailRow("Signal", "${ap.signalStrength} dBm")
                                ApDetailRow("Security", ap.securityType.label)
                                ApDetailRow("Freq", "${ap.frequency} MHz")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThreatAlertCard(threat: WifiDetectionResult) {
    val severityColor = when (threat.confidence) {
        Confidence.HIGH -> Color(0xFFEF4444)
        Confidence.MEDIUM -> Color(0xFFF59E0B)
        Confidence.LOW -> Color(0xFF10B981)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = severityColor.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = severityColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    threat.threatType.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    threat.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            ConfidenceBadge(threat.confidence, severityColor)
        }
    }
}

@Composable
private fun ProbePrivacyCard(status: com.bp22intel.edgesentinel.detection.wifi.ProbePrivacyStatus) {
    val riskColor = when (status.probeLeakRisk) {
        ProbeLeakRisk.HIGH -> Color(0xFFEF4444)
        ProbeLeakRisk.MEDIUM -> Color(0xFFF59E0B)
        ProbeLeakRisk.LOW -> Color(0xFF10B981)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Probe Request Privacy",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(riskColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        status.probeLeakRisk.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = riskColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            ProbeDetailRow(
                "MAC Randomization",
                if (status.macRandomizationEnabled) "Enabled" else "Disabled",
                if (status.macRandomizationEnabled) Color(0xFF10B981) else Color(0xFFEF4444)
            )
            ProbeDetailRow(
                "Saved Networks",
                "${status.savedNetworkCount}",
                if (status.savedNetworkCount > 15) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurface
            )
            if (status.openSavedNetworks.isNotEmpty()) {
                ProbeDetailRow(
                    "Open Networks",
                    status.openSavedNetworks.take(3).joinToString(", "),
                    Color(0xFFF59E0B)
                )
            }
            if (status.recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                status.recommendations.forEach { rec ->
                    Text(
                        "• $rec",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AccessPointCard(
    ap: ObservedAp,
    threatLevel: ApThreatLevel,
    isTrusted: Boolean = false,
    onToggleTrust: () -> Unit = {}
) {
    val threatColor = when {
        isTrusted -> Color(0xFF06B6D4) // Cyan for trusted
        threatLevel == ApThreatLevel.SAFE -> Color(0xFF10B981)
        threatLevel == ApThreatLevel.SUSPICIOUS -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Threat level indicator dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(threatColor)
            )
            Spacer(modifier = Modifier.width(12.dp))

            // Security icon
            Icon(
                imageVector = if (ap.securityType == SecurityType.OPEN)
                    Icons.Default.LockOpen else Icons.Default.Lock,
                contentDescription = null,
                tint = if (ap.securityType == SecurityType.OPEN)
                    Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        ap.ssid.ifBlank { "(Hidden Network)" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (isTrusted) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "TRUSTED",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF06B6D4),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    "${ap.bssid} · ${ap.securityType.label} · ${ap.frequency} MHz",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${ap.signalStrength} dBm",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                // Trust/untrust button
                TextButton(
                    onClick = onToggleTrust,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        if (isTrusted) "Untrust" else "Trust",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isTrusted) Color(0xFFF59E0B) else Color(0xFF06B6D4)
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfidenceBadge(confidence: Confidence, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            confidence.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun ApDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ProbeDetailRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}
