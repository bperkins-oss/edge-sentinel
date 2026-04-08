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

package com.bp22intel.edgesentinel.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.bp22intel.edgesentinel.MainActivity
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import com.bp22intel.edgesentinel.service.MonitoringService

class ThreatStatusWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val threatLevel = MonitoringService.threatLevel.value
        val isRunning = MonitoringService.isRunning.value

        provideContent {
            ThreatStatusContent(
                threatLevel = threatLevel,
                isRunning = isRunning
            )
        }
    }
}

@Composable
private fun ThreatStatusContent(
    threatLevel: ThreatLevel,
    isRunning: Boolean
) {
    val bgColor = ColorProvider(
        day = androidx.compose.ui.graphics.Color(0xFF0D1117),
        night = androidx.compose.ui.graphics.Color(0xFF0D1117)
    )
    val textColor = ColorProvider(
        day = androidx.compose.ui.graphics.Color(0xFFF0F6FC),
        night = androidx.compose.ui.graphics.Color(0xFFF0F6FC)
    )
    val subtextColor = ColorProvider(
        day = androidx.compose.ui.graphics.Color(0xFF8B949E),
        night = androidx.compose.ui.graphics.Color(0xFF8B949E)
    )
    val statusColor = when (threatLevel) {
        ThreatLevel.CLEAR -> ColorProvider(
            day = androidx.compose.ui.graphics.Color(0xFF10B981),
            night = androidx.compose.ui.graphics.Color(0xFF10B981)
        )
        ThreatLevel.SUSPICIOUS -> ColorProvider(
            day = androidx.compose.ui.graphics.Color(0xFFF59E0B),
            night = androidx.compose.ui.graphics.Color(0xFFF59E0B)
        )
        ThreatLevel.THREAT -> ColorProvider(
            day = androidx.compose.ui.graphics.Color(0xFFEF4444),
            night = androidx.compose.ui.graphics.Color(0xFFEF4444)
        )
    }

    val statusLabel = when (threatLevel) {
        ThreatLevel.CLEAR -> "CLEAR"
        ThreatLevel.SUSPICIOUS -> "SUSPICIOUS"
        ThreatLevel.THREAT -> "THREAT"
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(bgColor)
            .clickable(actionStartActivity<MainActivity>())
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Edge Sentinel",
                style = TextStyle(
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Status indicator dot
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(12.dp)
                        .background(statusColor)
                )
                Spacer(modifier = GlanceModifier.width(8.dp))
                Text(
                    text = statusLabel,
                    style = TextStyle(
                        color = statusColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(6.dp))

            Text(
                text = if (isRunning) "Monitoring active" else "Monitoring inactive",
                style = TextStyle(
                    color = subtextColor,
                    fontSize = 10.sp
                )
            )
        }
    }
}

class ThreatStatusWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ThreatStatusWidget()
}
