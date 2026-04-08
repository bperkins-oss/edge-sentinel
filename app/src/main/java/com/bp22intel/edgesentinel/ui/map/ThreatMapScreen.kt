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

package com.bp22intel.edgesentinel.ui.map

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bp22intel.edgesentinel.detection.geo.GeolocatedThreat
import com.bp22intel.edgesentinel.domain.model.SensorCategory
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import com.bp22intel.edgesentinel.ui.theme.BackgroundPrimary
import com.bp22intel.edgesentinel.ui.theme.StatusClear
import com.bp22intel.edgesentinel.ui.theme.StatusDangerous
import com.bp22intel.edgesentinel.ui.theme.StatusSuspicious
import com.bp22intel.edgesentinel.ui.theme.Surface
import com.bp22intel.edgesentinel.ui.theme.TextPrimary
import com.bp22intel.edgesentinel.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreatMapScreen(
    viewModel: ThreatMapViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val threats by viewModel.geolocatedThreats.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    var selectedThreat by remember { mutableStateOf<GeolocatedThreat?>(null) }
    
    // Auto-zoom based on threat distances, default to 500m
    val maxDistance = remember(threats) {
        if (threats.isEmpty()) 500.0
        else max(500.0, threats.maxOfOrNull { it.accuracyMeters * 2 } ?: 500.0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "THREAT MAP",
                        color = StatusClear,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = StatusClear
                        )
                    }
                },
                actions = {
                    Text(
                        text = "${threats.size} contacts",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundPrimary
                )
            )
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tactical Radar Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                TacticalRadarCanvas(
                    threats = threats,
                    userLocation = userLocation,
                    maxRangeMeters = maxDistance,
                    onThreatClick = { threat ->
                        selectedThreat = threat
                    }
                )
                
                // Selected threat popup
                selectedThreat?.let { threat ->
                    ThreatInfoPopup(
                        threat = threat,
                        onDismiss = { selectedThreat = null },
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
            }

            // Legend
            ThreatMapLegend(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun TacticalRadarCanvas(
    threats: List<GeolocatedThreat>,
    userLocation: Pair<Double, Double>,
    maxRangeMeters: Double,
    onThreatClick: (GeolocatedThreat) -> Unit
) {
    // Radar sweep animation
    val infiniteTransition = rememberInfiniteTransition(label = "radar_sweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_angle"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { /* Handle general canvas clicks if needed */ }
    ) {
        val center = size.center
        val radius = min(size.width, size.height) / 2 - 32.dp.toPx()
        
        // Draw radar background
        drawRadarGrid(center, radius, maxRangeMeters)
        
        // Draw compass bearings
        drawCompassBearings(center, radius)
        
        // Draw user position (pulsing center dot)
        drawUserPosition(center)
        
        // Draw threats
        drawThreats(threats, center, radius, maxRangeMeters, userLocation, onThreatClick)
        
        // Draw radar sweep
        drawRadarSweep(center, radius, sweepAngle)
    }
}

private fun DrawScope.drawRadarGrid(center: Offset, radius: Float, maxRangeMeters: Double) {
    val gridColor = StatusClear.copy(alpha = 0.3f)
    
    // Dynamic concentric circles — always 4 rings evenly spaced to the edge
    val ringCount = 4
    val visibleRanges = (1..ringCount).map { i ->
        (maxRangeMeters * i / ringCount)
    }
    
    visibleRanges.forEach { range ->
        val circleRadius = (range / maxRangeMeters).toFloat() * radius
        
        // Draw circle
        drawCircle(
            color = gridColor,
            radius = circleRadius,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
        
        // Draw distance label on each ring
        val label = when {
            range >= 10000.0 -> "${"%.1f".format(range / 1000)}km"
            range >= 1000.0 -> "${"%.1f".format(range / 1000)}km"
            range >= 100.0 -> "${range.toInt()}m"
            else -> "${range.toInt()}m"
        }.replace(".0km", "km") // clean up "1.0km" → "1km"
        drawIntoCanvas { canvas ->
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb(180, 88, 166, 92) // StatusClear with alpha
                textSize = 10.dp.toPx()
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD
                )
            }
            // Position label just above the ring on the right side
            val textWidth = paint.measureText(label)
            canvas.nativeCanvas.drawText(
                label,
                center.x + 4.dp.toPx(),
                center.y - circleRadius - 3.dp.toPx(),
                paint
            )
        }
    }
    
    // Cross hairs
    drawLine(
        color = gridColor,
        start = Offset(center.x, center.y - radius),
        end = Offset(center.x, center.y + radius),
        strokeWidth = 1.dp.toPx()
    )
    drawLine(
        color = gridColor,
        start = Offset(center.x - radius, center.y),
        end = Offset(center.x + radius, center.y),
        strokeWidth = 1.dp.toPx()
    )
}

private fun DrawScope.drawCompassBearings(center: Offset, radius: Float) {
    val bearingColor = StatusClear.copy(alpha = 0.6f)
    val bearings = listOf(
        "N" to 0f,
        "E" to 90f,
        "S" to 180f,
        "W" to 270f
    )
    
    bearings.forEach { (label, degrees) ->
        val radians = Math.toRadians(degrees.toDouble())
        val x = center.x + radius * 0.9f * sin(radians).toFloat()
        val y = center.y - radius * 0.9f * cos(radians).toFloat()
        
        // Draw small bearing indicators instead of text to avoid nativeCanvas issues
        drawCircle(
            color = bearingColor,
            radius = 4.dp.toPx(),
            center = Offset(x, y)
        )
        // In a real implementation, you would use drawIntoCanvas for text
    }
}

private fun DrawScope.drawUserPosition(center: Offset) {
    // Pulsing green dot at center
    drawCircle(
        color = StatusClear,
        radius = 8.dp.toPx(),
        center = center
    )
    drawCircle(
        color = StatusClear.copy(alpha = 0.3f),
        radius = 12.dp.toPx(),
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )
}

private fun DrawScope.drawThreats(
    threats: List<GeolocatedThreat>,
    center: Offset,
    radius: Float,
    maxRangeMeters: Double,
    userLocation: Pair<Double, Double>,
    onThreatClick: (GeolocatedThreat) -> Unit
) {
    threats.forEach { threat ->
        // Calculate relative position (simplified - in real app would use proper geo calculations)
        val distance = calculateDistance(userLocation, Pair(threat.latitude, threat.longitude))
        if (distance <= maxRangeMeters) {
            val normalizedDistance = (distance / maxRangeMeters).toFloat()
            val threatRadius = normalizedDistance * radius
            
            // Simple bearing calculation (would need proper geo calculations in real app)
            val bearing = calculateBearing(userLocation, Pair(threat.latitude, threat.longitude))
            val radians = Math.toRadians(bearing)
            
            val threatX = center.x + threatRadius * sin(radians).toFloat()
            val threatY = center.y - threatRadius * cos(radians).toFloat()
            val threatCenter = Offset(threatX, threatY)
            
            // Threat color based on level
            val threatColor = when (threat.threatLevel) {
                ThreatLevel.CLEAR -> StatusClear
                ThreatLevel.SUSPICIOUS -> StatusSuspicious
                ThreatLevel.THREAT -> StatusDangerous
            }
            
            // Accuracy circle (uncertainty)
            val accuracyRadius = (threat.accuracyMeters / maxRangeMeters).toFloat() * radius
            drawCircle(
                color = threatColor.copy(alpha = 0.1f),
                radius = accuracyRadius,
                center = threatCenter
            )
            
            // Threat dot - size based on accuracy (larger = less precise)
            val dotSize = (8 + (threat.accuracyMeters / 100).coerceIn(0.0, 8.0)).dp.toPx()
            drawCircle(
                color = threatColor,
                radius = dotSize,
                center = threatCenter
            )
            
            // Category indicator
            drawCircle(
                color = Color.Black,
                radius = dotSize * 0.5f,
                center = threatCenter
            )
        }
    }
}

private fun DrawScope.drawRadarSweep(center: Offset, radius: Float, sweepAngle: Float) {
    val sweepColor = StatusClear.copy(alpha = 0.6f)
    
    // Create sweep path
    val path = Path().apply {
        moveTo(center.x, center.y)
        lineTo(
            center.x + radius * sin(Math.toRadians(sweepAngle.toDouble())).toFloat(),
            center.y - radius * cos(Math.toRadians(sweepAngle.toDouble())).toFloat()
        )
    }
    
    rotate(degrees = sweepAngle, pivot = center) {
        drawLine(
            color = sweepColor,
            start = center,
            end = Offset(center.x, center.y - radius),
            strokeWidth = 3.dp.toPx()
        )
    }
}

@Composable
private fun ThreatInfoPopup(
    threat: GeolocatedThreat,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(200.dp)
            .clickable { onDismiss() },
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = threat.label,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = threat.category.label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            
            Text(
                text = "±${threat.accuracyMeters.toInt()}m",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            
            threat.signalStrengthDbm?.let { rssi ->
                Text(
                    text = "${rssi}dBm",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            Text(
                text = formatTimestamp(threat.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun ThreatMapLegend(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(
                color = StatusClear,
                label = "Clear"
            )
            LegendItem(
                color = StatusSuspicious,
                label = "Suspicious"
            )
            LegendItem(
                color = StatusDangerous,
                label = "Threat"
            )
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

// Helper functions for geo calculations (simplified - would need proper implementation)
private fun calculateDistance(
    location1: Pair<Double, Double>,
    location2: Pair<Double, Double>
): Double {
    // Simplified distance calculation - in real app would use Haversine formula
    val deltaLat = location2.first - location1.first
    val deltaLng = location2.second - location1.second
    return kotlin.math.sqrt(deltaLat * deltaLat + deltaLng * deltaLng) * 111000 // Rough conversion to meters
}

private fun calculateBearing(
    location1: Pair<Double, Double>,
    location2: Pair<Double, Double>
): Double {
    // Simplified bearing calculation - in real app would use proper geo calculations
    val deltaLng = location2.second - location1.second
    val deltaLat = location2.first - location1.first
    return Math.toDegrees(kotlin.math.atan2(deltaLng, deltaLat))
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = (now - timestamp) / 1000
    
    return when {
        diff < 60 -> "${diff}s ago"
        diff < 3600 -> "${diff / 60}m ago"
        diff < 86400 -> "${diff / 3600}h ago"
        else -> "${diff / 86400}d ago"
    }
}