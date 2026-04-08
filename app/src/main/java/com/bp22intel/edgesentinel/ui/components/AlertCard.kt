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

package com.bp22intel.edgesentinel.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppMaybe
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.domain.model.Confidence
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import com.bp22intel.edgesentinel.domain.model.ThreatType
import com.bp22intel.edgesentinel.ui.theme.EdgeSentinelTheme
import com.bp22intel.edgesentinel.ui.theme.Surface
import com.bp22intel.edgesentinel.ui.theme.TextSecondary

private fun threatTypeIcon(type: ThreatType): ImageVector {
    return when (type) {
        ThreatType.FAKE_BTS -> Icons.Filled.SignalCellular4Bar
        ThreatType.NETWORK_DOWNGRADE -> Icons.Filled.NetworkCheck
        ThreatType.SILENT_SMS -> Icons.Filled.Sms
        ThreatType.TRACKING_PATTERN -> Icons.Filled.TrackChanges
        ThreatType.CIPHER_ANOMALY -> Icons.Filled.GppBad
        ThreatType.SIGNAL_ANOMALY -> Icons.Filled.GppMaybe
        ThreatType.NR_ANOMALY -> Icons.Filled.NetworkCheck
    }
}

private fun threatTypeLabel(type: ThreatType): String {
    return when (type) {
        ThreatType.FAKE_BTS -> "Fake BTS"
        ThreatType.NETWORK_DOWNGRADE -> "Downgrade"
        ThreatType.SILENT_SMS -> "Silent SMS"
        ThreatType.TRACKING_PATTERN -> "Tracking"
        ThreatType.CIPHER_ANOMALY -> "Cipher"
        ThreatType.SIGNAL_ANOMALY -> "Signal"
        ThreatType.NR_ANOMALY -> "5G NR"
    }
}

private fun formatRelativeTime(timestampMs: Long): String {
    val now = System.currentTimeMillis()
    val diffMs = now - timestampMs
    val diffSec = diffMs / 1000
    val diffMin = diffSec / 60
    val diffHour = diffMin / 60
    val diffDay = diffHour / 24

    return when {
        diffSec < 60 -> "just now"
        diffMin < 60 -> "${diffMin} min ago"
        diffHour < 24 -> "${diffHour} hour${if (diffHour != 1L) "s" else ""} ago"
        diffDay < 7 -> "${diffDay} day${if (diffDay != 1L) "s" else ""} ago"
        else -> "${diffDay / 7} week${if (diffDay / 7 != 1L) "s" else ""} ago"
    }
}

@Composable
fun AlertCard(
    alert: Alert,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = threatTypeIcon(alert.threatType),
                contentDescription = threatTypeLabel(alert.threatType),
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = threatTypeLabel(alert.threatType),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    StatusBadge(
                        text = alert.severity.label.uppercase(),
                        threatLevel = alert.severity
                    )
                }

                Text(
                    text = alert.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2
                )

                Text(
                    text = formatRelativeTime(alert.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
private fun AlertCardPreview() {
    EdgeSentinelTheme {
        AlertCard(
            alert = Alert(
                id = 1,
                timestamp = System.currentTimeMillis() - 300_000,
                threatType = ThreatType.FAKE_BTS,
                severity = ThreatLevel.THREAT,
                confidence = Confidence.HIGH,
                summary = "Suspicious base station detected with unusual signal pattern",
                detailsJson = "{}",
                cellId = 12345
            )
        )
    }
}
