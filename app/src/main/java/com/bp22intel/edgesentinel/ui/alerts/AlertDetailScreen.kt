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

package com.bp22intel.edgesentinel.ui.alerts

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bp22intel.edgesentinel.domain.model.Confidence
import com.bp22intel.edgesentinel.domain.model.ThreatType
import com.bp22intel.edgesentinel.ui.components.StatusBadge
import com.bp22intel.edgesentinel.ui.theme.AccentBlue
import com.bp22intel.edgesentinel.ui.theme.BackgroundPrimary
import com.bp22intel.edgesentinel.ui.theme.StatusClear
import com.bp22intel.edgesentinel.ui.theme.StatusSuspicious
import com.bp22intel.edgesentinel.ui.theme.StatusThreat
import com.bp22intel.edgesentinel.ui.theme.Surface
import com.bp22intel.edgesentinel.ui.theme.SurfaceVariant
import com.bp22intel.edgesentinel.ui.theme.TextPrimary
import com.bp22intel.edgesentinel.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun threatTypeLabel(type: ThreatType): String {
    return when (type) {
        ThreatType.FAKE_BTS -> "Fake Base Station (IMSI Catcher)"
        ThreatType.NETWORK_DOWNGRADE -> "Network Downgrade Attack"
        ThreatType.SILENT_SMS -> "Silent SMS (Type-0)"
        ThreatType.TRACKING_PATTERN -> "Location Tracking Pattern"
        ThreatType.CIPHER_ANOMALY -> "Cipher/Encryption Anomaly"
        ThreatType.SIGNAL_ANOMALY -> "Signal Anomaly"
        ThreatType.NR_ANOMALY -> "5G NR Anomaly"
    }
}

private fun confidenceProgress(confidence: Confidence): Float {
    return when (confidence) {
        Confidence.LOW -> 0.33f
        Confidence.MEDIUM -> 0.66f
        Confidence.HIGH -> 1.0f
    }
}

private fun confidenceColor(confidence: Confidence) = when (confidence) {
    Confidence.LOW -> StatusClear
    Confidence.MEDIUM -> StatusSuspicious
    Confidence.HIGH -> StatusThreat
}

private fun parseDetailsJson(json: String): Map<String, String> {
    return try {
        val content = json.trim().removePrefix("{").removeSuffix("}")
        if (content.isBlank()) return emptyMap()

        val result = mutableMapOf<String, String>()
        // Simple JSON key-value parser for flat objects
        val regex = "\"([^\"]+)\"\\s*:\\s*\"([^\"]*?)\"".toRegex()
        regex.findAll(content).forEach { match ->
            result[match.groupValues[1]] = match.groupValues[2]
        }
        result
    } catch (_: Exception) {
        mapOf("raw" to json)
    }
}

@Composable
fun AlertDetailScreen(
    alertId: Long,
    viewModel: AlertDetailViewModel = hiltViewModel()
) {
    val alert by viewModel.alert.collectAsState()
    val context = LocalContext.current
    var technicalDetailsExpanded by remember { mutableStateOf(false) }

    val currentAlert = alert

    if (currentAlert == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Loading alert...",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header: threat type and severity badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = threatTypeLabel(currentAlert.threatType),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            StatusBadge(
                text = currentAlert.severity.label.uppercase(),
                threatLevel = currentAlert.severity
            )
        }

        // Timestamp
        Text(
            text = SimpleDateFormat("MMM dd, yyyy  HH:mm:ss", Locale.getDefault())
                .format(Date(currentAlert.timestamp)),
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        // "What was detected" section
        Card(
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "What was detected",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = currentAlert.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Confidence level indicator
        Card(
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Confidence Level",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = currentAlert.confidence.name,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = confidenceColor(currentAlert.confidence)
                    )
                }
                LinearProgressIndicator(
                    progress = { confidenceProgress(currentAlert.confidence) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = confidenceColor(currentAlert.confidence),
                    trackColor = SurfaceVariant
                )
            }
        }

        // Recommended action section
        Card(
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Recommended Action",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = viewModel.getRecommendedAction(currentAlert.threatType),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Expandable technical details section
        Card(
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { technicalDetailsExpanded = !technicalDetailsExpanded }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Technical Details",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Icon(
                        imageVector = if (technicalDetailsExpanded) {
                            Icons.Filled.ExpandLess
                        } else {
                            Icons.Filled.ExpandMore
                        },
                        contentDescription = if (technicalDetailsExpanded) "Collapse" else "Expand",
                        tint = TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                AnimatedVisibility(visible = technicalDetailsExpanded) {
                    val details = parseDetailsJson(currentAlert.detailsJson)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (details.isEmpty()) {
                            Text(
                                text = "No additional details available",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        } else {
                            details.forEach { (key, value) ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            SurfaceVariant,
                                            MaterialTheme.shapes.small
                                        )
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentBlue
                                    )
                                    Text(
                                        text = value,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Cell ID: ${currentAlert.cellId ?: "N/A"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Text(
                            text = "Alert ID: ${currentAlert.id}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Share button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = {
                    val shareText = buildString {
                        appendLine("Edge Sentinel Alert Report")
                        appendLine("=========================")
                        appendLine("Type: ${threatTypeLabel(currentAlert.threatType)}")
                        appendLine("Severity: ${currentAlert.severity.label}")
                        appendLine("Confidence: ${currentAlert.confidence.name}")
                        appendLine()
                        appendLine("Summary: ${currentAlert.summary}")
                        appendLine()
                        appendLine("Recommended Action:")
                        appendLine(viewModel.getRecommendedAction(currentAlert.threatType))
                        appendLine()
                        appendLine("Timestamp: ${
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                .format(Date(currentAlert.timestamp))
                        }")
                        appendLine("Cell ID: ${currentAlert.cellId ?: "N/A"}")
                    }

                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    context.startActivity(
                        Intent.createChooser(sendIntent, "Share Alert Report")
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share alert",
                    tint = AccentBlue
                )
            }
        }
    }
}
