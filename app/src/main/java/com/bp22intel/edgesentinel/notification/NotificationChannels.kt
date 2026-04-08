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

package com.bp22intel.edgesentinel.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build

object NotificationChannels {

    const val CRITICAL = "edge_sentinel_critical"
    const val WARNING = "edge_sentinel_warning"
    const val MONITORING = "edge_sentinel_monitoring"
    const val MESH = "edge_sentinel_mesh"

    fun createAll(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val critical = NotificationChannel(
            CRITICAL,
            "Critical Threat Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "High-severity threat detections requiring immediate attention"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 250, 200, 250, 200, 250)
            enableLights(true)
            lightColor = Color.RED
            setShowBadge(true)
        }

        val warning = NotificationChannel(
            WARNING,
            "Warning Alerts",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Suspicious activity that may require investigation"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 200, 100, 200)
            enableLights(true)
            lightColor = Color.YELLOW
            setShowBadge(true)
        }

        val monitoring = NotificationChannel(
            MONITORING,
            "Monitoring Status",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Persistent notification showing monitoring status"
            setShowBadge(false)
        }

        val mesh = NotificationChannel(
            MESH,
            "Mesh Peer Alerts",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Alerts from nearby Edge Sentinel mesh peers"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 150, 100, 150)
            setShowBadge(true)
        }

        manager.createNotificationChannels(listOf(critical, warning, monitoring, mesh))
    }
}
