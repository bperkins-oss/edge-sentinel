/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024-2026 BP22 Intel. All Rights Reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use of this software, in whole or in
 * part, is strictly prohibited without prior written permission from
 * BP22 Intel.
 */

package com.bp22intel.edgesentinel.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import com.bp22intel.edgesentinel.ui.theme.EdgeSentinelTheme
import com.bp22intel.edgesentinel.ui.theme.StatusClear
import com.bp22intel.edgesentinel.ui.theme.StatusSuspicious
import com.bp22intel.edgesentinel.ui.theme.StatusThreat

@Composable
fun ThreatIndicator(
    threatLevel: ThreatLevel,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp
) {
    val targetColor = when (threatLevel) {
        ThreatLevel.CLEAR -> StatusClear
        ThreatLevel.SUSPICIOUS -> StatusSuspicious
        ThreatLevel.THREAT -> StatusThreat
    }

    // Smooth color transition when threat level changes
    val baseColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 800),
        label = "threat_color"
    )

    val labelText = when (threatLevel) {
        ThreatLevel.CLEAR -> "CLEAR"
        ThreatLevel.SUSPICIOUS -> "SUSPICIOUS"
        ThreatLevel.THREAT -> "THREAT"
    }

    val infiniteTransition = rememberInfiniteTransition(label = "threat_pulse")

    // Main pulse alpha
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = if (threatLevel == ThreatLevel.THREAT) 0.3f else 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (threatLevel == ThreatLevel.THREAT) 800 else 3000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Radar sweep — expanding ring that fades out (active on SUSPICIOUS and THREAT)
    val radarProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (threatLevel == ThreatLevel.THREAT) 1200 else 2400,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_sweep"
    )

    val showRadar = threatLevel != ThreatLevel.CLEAR

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = this.size.minDimension / 2f

            // Outer glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        baseColor.copy(alpha = pulseAlpha * 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            // Radar pulse rings (expanding outward when threat detected)
            if (showRadar) {
                val radarRadius = radius * 0.3f + (radius * 0.7f * radarProgress)
                val radarAlpha = (1f - radarProgress) * 0.6f
                drawCircle(
                    color = baseColor.copy(alpha = radarAlpha),
                    radius = radarRadius,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Inner filled circle
            drawCircle(
                color = baseColor.copy(alpha = 0.15f),
                radius = radius * 0.75f,
                center = center
            )

            // Ring stroke
            drawCircle(
                color = baseColor.copy(alpha = pulseAlpha),
                radius = radius * 0.75f,
                center = center,
                style = Stroke(width = 4.dp.toPx())
            )
        }

        Text(
            text = labelText,
            color = baseColor,
            fontSize = when (threatLevel) {
                ThreatLevel.SUSPICIOUS -> 16.sp
                else -> 20.sp
            },
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
private fun ThreatIndicatorClearPreview() {
    EdgeSentinelTheme {
        ThreatIndicator(threatLevel = ThreatLevel.CLEAR)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
private fun ThreatIndicatorThreatPreview() {
    EdgeSentinelTheme {
        ThreatIndicator(threatLevel = ThreatLevel.THREAT)
    }
}
