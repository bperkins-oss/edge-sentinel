/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024-2026 BP22 Intel. All Rights Reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use of this software, in whole or in
 * part, is strictly prohibited without prior written permission from
 * BP22 Intel.
 */

package com.bp22intel.edgesentinel.ui.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp22intel.edgesentinel.data.sensor.CellInfoCollector
import com.bp22intel.edgesentinel.detection.engine.DemoDataGenerator
import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.domain.model.CellTower
import com.bp22intel.edgesentinel.domain.model.FusedThreatAssessment
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import com.bp22intel.edgesentinel.domain.repository.AlertRepository
import com.bp22intel.edgesentinel.domain.repository.CellRepository
import com.bp22intel.edgesentinel.domain.repository.ScanRepository
import com.bp22intel.edgesentinel.fusion.DashboardPosture
import com.bp22intel.edgesentinel.fusion.OverallThreatDashboard
import com.bp22intel.edgesentinel.fusion.SensorFusionEngine
import com.bp22intel.edgesentinel.service.MonitoringService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val alertRepository: AlertRepository,
    private val cellRepository: CellRepository,
    private val scanRepository: ScanRepository,
    private val demoDataGenerator: DemoDataGenerator,
    private val cellInfoCollector: CellInfoCollector,
    private val sensorFusionEngine: SensorFusionEngine,
    private val overallThreatDashboard: OverallThreatDashboard,
    private val threatAnalyst: com.bp22intel.edgesentinel.analysis.ThreatAnalyst
) : ViewModel() {

    val currentThreatLevel: StateFlow<ThreatLevel> = MonitoringService.threatLevel

    val fusedAssessment: StateFlow<FusedThreatAssessment> = sensorFusionEngine.currentAssessment

    val dashboardPosture: StateFlow<DashboardPosture> = sensorFusionEngine.currentAssessment
        .map { assessment -> overallThreatDashboard.computePosture(assessment) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            overallThreatDashboard.computePosture(FusedThreatAssessment.clear())
        )

    private val _currentCell = MutableStateFlow<CellTower?>(null)
    val currentCell: StateFlow<CellTower?> = _currentCell.asStateFlow()

    val recentAlerts: StateFlow<List<Alert>> = alertRepository.getRecentAlerts(10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isMonitoring: StateFlow<Boolean> = MonitoringService.isRunning

    private val _monitoringStartTime = MutableStateFlow(0L)
    val monitoringStartTime: StateFlow<Long> = _monitoringStartTime.asStateFlow()

    private val _isDemoMode = MutableStateFlow(false)
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    /** AI situation brief — real analysis from ThreatAnalyst engine */
    val situationBrief: StateFlow<com.bp22intel.edgesentinel.analysis.SituationBrief> =
        kotlinx.coroutines.flow.combine(recentAlerts, _currentCell) { alerts, cell ->
            threatAnalyst.analyzeSituation(
                alerts = alerts,
                cellInfo = cell,
                isMoving = false // TODO: wire to motion sensor
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            com.bp22intel.edgesentinel.analysis.SituationBrief(
                summary = "Initializing analysis...",
                overallRisk = com.bp22intel.edgesentinel.analysis.RiskLevel.LOW,
                topConcerns = emptyList(),
                recommendations = emptyList(),
                allClear = true
            )
        )

    init {
        viewModelScope.launch {
            MonitoringService.isRunning.collect { running ->
                if (running && _monitoringStartTime.value == 0L) {
                    _monitoringStartTime.value = System.currentTimeMillis()
                } else if (!running) {
                    _monitoringStartTime.value = 0L
                }
            }
        }

        viewModelScope.launch {
            cellRepository.getAllCells().collect { cells ->
                if (cells.isNotEmpty()) {
                    _currentCell.value = cells.maxByOrNull { it.lastSeen }
                }
            }
        }

        // Try to get current cell info on launch
        viewModelScope.launch {
            try {
                val cells = cellInfoCollector.getCurrentCellInfo()
                if (cells.isNotEmpty()) {
                    _currentCell.value = cells.first()
                }
            } catch (_: Exception) {
                // Permission may not be granted yet
            }
        }
    }

    fun toggleMonitoring(context: Context) {
        if (isMonitoring.value) {
            MonitoringService.stop(context)
        } else {
            _monitoringStartTime.value = System.currentTimeMillis()
            MonitoringService.start(context)
        }
    }

    fun forceScan() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val cells = cellInfoCollector.getCurrentCellInfo()
                if (cells.isNotEmpty()) {
                    _currentCell.value = cells.first()
                    for (cell in cells) {
                        cellRepository.insertOrUpdateCell(cell)
                    }
                }
            } catch (_: Exception) {
                // Permission may not be granted yet
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun loadDemoData() {
        viewModelScope.launch {
            _isDemoMode.value = true

            val demoCells = demoDataGenerator.generateDemoCells()
            for (cell in demoCells) {
                cellRepository.insertOrUpdateCell(cell)
            }
            _currentCell.value = demoCells.firstOrNull()

            val demoAlerts = demoDataGenerator.generateDemoAlerts()
            for (alert in demoAlerts) {
                alertRepository.insertAlert(alert)
            }
        }
    }
}
