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

package com.bp22intel.edgesentinel.ui.mesh

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bp22intel.edgesentinel.domain.model.Confidence
import com.bp22intel.edgesentinel.domain.model.SensorCategory
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import com.bp22intel.edgesentinel.mesh.MeshAlert
import com.bp22intel.edgesentinel.mesh.MeshAlertAggregator
import com.bp22intel.edgesentinel.mesh.MeshUiState
import com.bp22intel.edgesentinel.mesh.MeshViewModel
import com.bp22intel.edgesentinel.ui.components.SectionHeader
import com.bp22intel.edgesentinel.ui.theme.BackgroundPrimary
import com.bp22intel.edgesentinel.ui.theme.Surface
import com.bp22intel.edgesentinel.ui.theme.SurfaceVariant
import com.bp22intel.edgesentinel.ui.theme.StatusClear
import com.bp22intel.edgesentinel.ui.theme.StatusElevated
import com.bp22intel.edgesentinel.ui.theme.StatusDangerous
import com.bp22intel.edgesentinel.ui.theme.TextPrimary
import com.bp22intel.edgesentinel.ui.theme.TextSecondary
import com.bp22intel.edgesentinel.ui.theme.AccentBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val MeshCyan = Color(0xFF06B6D4)

// Data classes for the enhanced UI (extending existing MeshUiState)
data class MeshPeer(
    val id: String,
    val displayName: String,
    val rssi: Int,
    val lastSeen: Long,
    val threatLevel: ThreatLevel,
    val isConnected: Boolean
)

data class SharedAlert(
    val id: String,
    val peerId: String,
    val peerDisplayName: String,
    val alertType: String,
    val description: String,
    val timestamp: Long,
    val isCorroborated: Boolean
)

data class ConsensusAlert(
    val threatDescription: String,
    val detectingPeerCount: Int,
    val totalNearbyPeers: Int,
    val confidenceLevel: String,
    val firstDetected: Long,
    val category: SensorCategory
)

data class MeshStats(
    val totalPeersToday: Int,
    val alertsShared: Int,
    val alertsReceived: Int,
    val correlatedDetections: Int
)

@Composable
fun MeshScreen(
    onBack: () -> Unit = {},
    viewModel: MeshViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Mock data for demo purposes (enhance later when backend supports these features)
    val mockPeers = listOf(
        MeshPeer("abc12345-...", "abc12345", -65, System.currentTimeMillis() - 5000, ThreatLevel.CLEAR, true),
        MeshPeer("def67890-...", "def67890", -78, System.currentTimeMillis() - 12000, ThreatLevel.SUSPICIOUS, true),
        MeshPeer("ghi09876-...", "ghi09876", -82, System.currentTimeMillis() - 8000, ThreatLevel.THREAT, false)
    )
    
    val mockConsensus = if (uiState.correlatedAlerts.isNotEmpty()) {
        uiState.correlatedAlerts.map { correlated ->
            ConsensusAlert(
                threatDescription = "Suspicious tower CID ${correlated.cellCid ?: "unknown"}",
                detectingPeerCount = correlated.peerCount,
                totalNearbyPeers = mockPeers.size + 1, // +1 for this device
                confidenceLevel = when {
                    correlated.peerCount >= 3 -> "Confirmed"
                    correlated.peerCount >= 2 -> "Corroborated"
                    else -> "Unconfirmed"
                },
                firstDetected = correlated.firstSeen,
                category = SensorCategory.CELLULAR
            )
        }
    } else emptyList()
    
    val mockSharedAlerts = uiState.recentMeshAlerts.map { alert ->
        SharedAlert(
            id = alert.messageId,
            peerId = alert.deviceId,
            peerDisplayName = alert.deviceId.take(8),
            alertType = alert.threatType.name,
            description = alert.summary,
            timestamp = alert.timestamp,
            isCorroborated = false // Would check if local device also detected this
        )
    }
    
    val meshStats = MeshStats(
        totalPeersToday = mockPeers.size,
        alertsShared = 5,
        alertsReceived = uiState.totalAlertsReceived,
        correlatedDetections = uiState.corroboratedAlertCount
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mesh Network") },
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
        // Top Status Bar
        item { 
            MeshStatusCard(
                state = uiState,
                peerCount = mockPeers.size,
                onStart = viewModel::startMesh,
                onStop = viewModel::stopMesh
            ) 
        }

        // Error message
        uiState.error?.let { errorMsg ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEF4444).copy(alpha = 0.15f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = errorMsg,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFEF4444),
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.dismissError() }) {
                            Text("Dismiss", color = Color(0xFFEF4444))
                        }
                    }
                }
            }
        }
        
        // Nearby Peers Section
        item {
            SectionHeader(title = "Nearby Peers")
        }
        
        if (uiState.isActive) {
            if (mockPeers.isNotEmpty()) {
                items(mockPeers, key = { it.id }) { peer ->
                    NearbyPeerCard(peer)
                }
            } else {
                item {
                    ScanningEmptyState()
                }
            }
        } else {
            item {
                DisabledEmptyState()
            }
        }
        
        // Team Threat Consensus Section
        if (mockConsensus.isNotEmpty()) {
            item {
                SectionHeader(title = "Team Threat Consensus")
            }
            items(mockConsensus, key = { "${it.threatDescription}_${it.firstDetected}" }) { consensus ->
                ConsensusAlertCard(consensus)
            }
        }
        
        // Shared Alerts Feed Section
        if (mockSharedAlerts.isNotEmpty()) {
            item {
                SectionHeader(title = "Shared Alerts")
            }
            items(mockSharedAlerts, key = { it.id }) { alert ->
                SharedAlertCard(alert)
            }
        }
        
        // Mesh Stats Footer
        item {
            MeshStatsCard(meshStats)
        }
        
        // Bottom spacer
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    }
}

@Composable
private fun MeshStatusCard(
    state: MeshUiState,
    peerCount: Int,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = if (state.isActive) MeshCyan else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Mesh Network",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Status indicator
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (state.isActive) StatusClear else TextSecondary
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (state.isActive) "Active — Scanning" else "Disabled",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
                
                Switch(
                    checked = state.isActive,
                    onCheckedChange = { enabled ->
                        if (enabled) onStart() else onStop()
                    }
                )
            }
            
            if (state.isActive) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "$peerCount peers nearby",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MeshCyan
                    )
                    Text(
                        "BLE discovery active",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun NearbyPeerCard(peer: MeshPeer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Device identifier
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = peer.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = formatLastSeen(peer.lastSeen),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Signal strength (RSSI bars)
            RssiIndicator(rssi = peer.rssi)
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Threat level indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        when (peer.threatLevel) {
                            ThreatLevel.CLEAR -> StatusClear
                            ThreatLevel.SUSPICIOUS -> StatusElevated
                            ThreatLevel.THREAT -> StatusDangerous
                        }
                    )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Connection quality
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = "Connection quality",
                tint = if (peer.isConnected) MeshCyan else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun RssiIndicator(rssi: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // Calculate signal strength bars (4 bars max)
        val bars = when {
            rssi > -60 -> 4
            rssi > -70 -> 3
            rssi > -80 -> 2
            rssi > -90 -> 1
            else -> 0
        }
        
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height((6 + index * 2).dp)
                    .background(
                        if (index < bars) MeshCyan else TextSecondary.copy(alpha = 0.3f),
                        RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}

@Composable
private fun ScanningEmptyState() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.People,
                contentDescription = null,
                tint = MeshCyan.copy(alpha = alpha),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Scanning for nearby Edge Sentinel devices...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DisabledEmptyState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.People,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enable mesh networking to discover nearby devices",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ConsensusAlertCard(consensus: ConsensusAlert) {
    val consensusColor = when (consensus.confidenceLevel) {
        "Confirmed" -> StatusDangerous
        "Corroborated" -> StatusElevated
        else -> StatusClear
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = consensusColor.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = consensusColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${consensus.detectingPeerCount} of ${consensus.totalNearbyPeers} peers also detect",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        consensus.threatDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Correlated detection — ${consensus.confidenceLevel.uppercase()} confidence",
                        style = MaterialTheme.typography.bodySmall,
                        color = consensusColor,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(consensusColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        consensus.confidenceLevel.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = consensusColor
                    )
                }
            }
        }
    }
}

@Composable
private fun SharedAlertCard(alert: SharedAlert) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Peer identifier badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MeshCyan.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    alert.peerDisplayName,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MeshCyan
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.alertType.replace("_", " "),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                if (alert.description.isNotBlank()) {
                    Text(
                        text = alert.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatTimestamp(alert.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                if (alert.isCorroborated) {
                    Text(
                        text = "✓ Corroborated",
                        style = MaterialTheme.typography.labelSmall,
                        color = StatusClear
                    )
                }
            }
        }
    }
}

@Composable
private fun MeshStatsCard(stats: MeshStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Mesh Statistics",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn("Peers Today", stats.totalPeersToday.toString())
                StatColumn("Alerts Shared", stats.alertsShared.toString())
                StatColumn("Alerts Received", stats.alertsReceived.toString())
                StatColumn("Correlated", stats.correlatedDetections.toString())
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MeshCyan
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

// Utility functions
private fun formatTimestamp(timestamp: Long): String {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(Date(timestamp))
}

private fun formatLastSeen(lastSeen: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - lastSeen
    return when {
        diff < 10_000 -> "${diff / 1000}s ago"
        diff < 60_000 -> "${diff / 1000}s ago" 
        diff < 3600_000 -> "${diff / 60_000}m ago"
        else -> "${diff / 3600_000}h ago"
    }
}
