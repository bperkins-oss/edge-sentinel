/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024-2026 BP22 Intel. All Rights Reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use of this software, in whole or in
 * part, is strictly prohibited without prior written permission from
 * BP22 Intel.
 */

package com.bp22intel.edgesentinel.service

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.bp22intel.edgesentinel.data.sensor.CellInfoCollector
import com.bp22intel.edgesentinel.data.sensor.TelephonyMonitor
import com.bp22intel.edgesentinel.detection.engine.ThreatDetectionEngine
import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.domain.model.Confidence
import com.bp22intel.edgesentinel.domain.model.DetectionResult
import com.bp22intel.edgesentinel.domain.model.ScanResult
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import com.bp22intel.edgesentinel.domain.repository.AlertRepository
import com.bp22intel.edgesentinel.domain.repository.CellRepository
import com.bp22intel.edgesentinel.domain.repository.ScanRepository
import com.bp22intel.edgesentinel.notification.NotificationChannels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MonitoringService : LifecycleService() {

    @Inject lateinit var cellInfoCollector: CellInfoCollector
    @Inject lateinit var telephonyMonitor: TelephonyMonitor
    @Inject lateinit var threatDetectionEngine: ThreatDetectionEngine
    @Inject lateinit var cellRepository: CellRepository
    @Inject lateinit var alertRepository: AlertRepository
    @Inject lateinit var scanRepository: ScanRepository
    @Inject lateinit var sensorFusionEngine: com.bp22intel.edgesentinel.fusion.SensorFusionEngine

    private var scanJob: Job? = null
    private lateinit var notificationManager: NotificationManager

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val ACTION_START = "com.bp22intel.edgesentinel.action.START"
        private const val ACTION_STOP = "com.bp22intel.edgesentinel.action.STOP"

        private const val ACTIVE_SCAN_INTERVAL_MS = 30_000L
        private const val PASSIVE_SCAN_INTERVAL_MS = 300_000L

        private val _threatLevel = MutableStateFlow(ThreatLevel.CLEAR)
        val threatLevel: StateFlow<ThreatLevel> = _threatLevel.asStateFlow()

        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

        fun start(context: Context) {
            val intent = Intent(context, MonitoringService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, MonitoringService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            ACTION_STOP -> {
                stopMonitoring()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                startForeground(NOTIFICATION_ID, buildStatusNotification(ThreatLevel.CLEAR))
                startMonitoring()
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        stopMonitoring()
        super.onDestroy()
    }

    private fun startMonitoring() {
        _isRunning.value = true
        telephonyMonitor.start()

        scanJob = lifecycleScope.launch {
            while (isActive) {
                performScan()
                val interval = if (_threatLevel.value == ThreatLevel.CLEAR) {
                    PASSIVE_SCAN_INTERVAL_MS
                } else {
                    ACTIVE_SCAN_INTERVAL_MS
                }
                delay(interval)
            }
        }
    }

    private fun stopMonitoring() {
        _isRunning.value = false
        _threatLevel.value = ThreatLevel.CLEAR
        scanJob?.cancel()
        scanJob = null
        telephonyMonitor.stop()
    }

    private suspend fun performScan() {
        val startTime = System.currentTimeMillis()

        try {
            val currentCells = cellInfoCollector.getCurrentCellInfo()
            val history = cellRepository.getAllCells().first()

            // Save observed cells
            for (cell in currentCells) {
                cellRepository.insertOrUpdateCell(cell)
            }

            // Run threat detection
            val detectionResults = threatDetectionEngine.runScan(
                cells = currentCells,
                history = history
            )

            // Determine overall threat level from detection results
            val scanThreatLevel = determineThreatLevel(detectionResults)
            _threatLevel.value = scanThreatLevel

            // Save scan result
            val durationMs = System.currentTimeMillis() - startTime
            val scanResult = ScanResult(
                timestamp = startTime,
                cellCount = currentCells.size,
                threatLevel = scanThreatLevel,
                durationMs = durationMs
            )
            scanRepository.insertScan(scanResult)

            // Create alerts for suspicious or threatening results
            for (result in detectionResults) {
                val severity = when {
                    result.score >= 0.7 -> ThreatLevel.THREAT
                    result.score >= 0.4 -> ThreatLevel.SUSPICIOUS
                    else -> ThreatLevel.CLEAR
                }

                if (severity == ThreatLevel.SUSPICIOUS || severity == ThreatLevel.THREAT) {
                    // Build enriched detailsJson with cell tower context
                    val detailsJson = org.json.JSONObject().apply {
                        // Copy detection indicators
                        result.details.forEach { (k, v) -> put(k, v) }
                        // Add current cell tower context
                        currentCells.firstOrNull()?.let { cell ->
                            put("cellId", cell.cid)
                            put("lac", cell.lacTac)
                            put("mcc", cell.mcc)
                            put("mnc", cell.mnc)
                            put("signalStrength", cell.signalStrength)
                            put("networkType", cell.networkType.name)
                            put("nearbyTowerCount", currentCells.size)
                        }
                    }.toString()

                    val alert = Alert(
                        timestamp = startTime,
                        threatType = result.threatType,
                        severity = severity,
                        confidence = result.confidence,
                        summary = result.summary,
                        detailsJson = detailsJson,
                        cellId = currentCells.firstOrNull()?.id,
                        acknowledged = false
                    )
                    alertRepository.insertAlert(alert)
                    showAlertNotification(alert)

                    // Feed into sensor fusion engine
                    val sensorCategory = when (result.threatType) {
                        com.bp22intel.edgesentinel.domain.model.ThreatType.FAKE_BTS,
                        com.bp22intel.edgesentinel.domain.model.ThreatType.NETWORK_DOWNGRADE,
                        com.bp22intel.edgesentinel.domain.model.ThreatType.SILENT_SMS,
                        com.bp22intel.edgesentinel.domain.model.ThreatType.CIPHER_ANOMALY,
                        com.bp22intel.edgesentinel.domain.model.ThreatType.SIGNAL_ANOMALY,
                        com.bp22intel.edgesentinel.domain.model.ThreatType.NR_ANOMALY ->
                            com.bp22intel.edgesentinel.domain.model.SensorCategory.CELLULAR
                        com.bp22intel.edgesentinel.domain.model.ThreatType.TRACKING_PATTERN ->
                            com.bp22intel.edgesentinel.domain.model.SensorCategory.BLUETOOTH
                    }
                    sensorFusionEngine.ingestDetection(
                        com.bp22intel.edgesentinel.fusion.ActiveDetection(
                            sensorCategory = sensorCategory,
                            detectionType = result.threatType.name,
                            description = result.summary,
                            score = result.score,
                            timestamp = startTime
                        )
                    )
                }
            }

            // Update foreground notification
            notificationManager.notify(NOTIFICATION_ID, buildStatusNotification(scanThreatLevel))
        } catch (e: Exception) {
            // Log but don't crash the scan loop
            val durationMs = System.currentTimeMillis() - startTime
            val scanResult = ScanResult(
                timestamp = startTime,
                cellCount = 0,
                threatLevel = ThreatLevel.CLEAR,
                durationMs = durationMs
            )
            scanRepository.insertScan(scanResult)
        }
    }

    private fun determineThreatLevel(results: List<DetectionResult>): ThreatLevel {
        if (results.isEmpty()) return ThreatLevel.CLEAR

        val maxScore = results.maxOf { it.score }
        return when {
            maxScore >= 0.7 -> ThreatLevel.THREAT
            maxScore >= 0.4 -> ThreatLevel.SUSPICIOUS
            else -> ThreatLevel.CLEAR
        }
    }

    private fun buildStatusNotification(threatLevel: ThreatLevel): Notification {
        val statusText = when (threatLevel) {
            ThreatLevel.CLEAR -> "All clear - monitoring cellular environment"
            ThreatLevel.SUSPICIOUS -> "Suspicious activity detected"
            ThreatLevel.THREAT -> "THREAT DETECTED - Cellular anomaly found"
        }

        val priority = when (threatLevel) {
            ThreatLevel.CLEAR -> NotificationCompat.PRIORITY_LOW
            ThreatLevel.SUSPICIOUS -> NotificationCompat.PRIORITY_DEFAULT
            ThreatLevel.THREAT -> NotificationCompat.PRIORITY_HIGH
        }

        return NotificationCompat.Builder(this, NotificationChannels.MONITORING)
            .setContentTitle("Edge Sentinel")
            .setContentText(statusText)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(priority)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun showAlertNotification(alert: Alert) {
        val notificationId = (alert.timestamp % Int.MAX_VALUE).toInt() + 100

        val title = when (alert.severity) {
            ThreatLevel.THREAT -> "THREAT: ${alert.threatType.name}"
            ThreatLevel.SUSPICIOUS -> "Suspicious: ${alert.threatType.name}"
            ThreatLevel.CLEAR -> return
        }

        val channelId = when (alert.severity) {
            ThreatLevel.THREAT -> NotificationChannels.CRITICAL
            else -> NotificationChannels.WARNING
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(alert.summary)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(
                if (alert.severity == ThreatLevel.THREAT) NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_DEFAULT
            )
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
