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

package com.bp22intel.edgesentinel.ui.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp22intel.edgesentinel.data.sensor.CellInfoCollector
import com.bp22intel.edgesentinel.detection.engine.DemoDataGenerator
import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.domain.model.CellTower
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import com.bp22intel.edgesentinel.domain.repository.AlertRepository
import com.bp22intel.edgesentinel.domain.repository.CellRepository
import com.bp22intel.edgesentinel.domain.repository.ScanRepository
import com.bp22intel.edgesentinel.service.MonitoringService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val alertRepository: AlertRepository,
    private val cellRepository: CellRepository,
    private val scanRepository: ScanRepository,
    private val demoDataGenerator: DemoDataGenerator,
    private val cellInfoCollector: CellInfoCollector
) : ViewModel() {

    val currentThreatLevel: StateFlow<ThreatLevel> = MonitoringService.threatLevel

    private val _currentCell = MutableStateFlow<CellTower?>(null)
    val currentCell: StateFlow<CellTower?> = _currentCell.asStateFlow()

    val recentAlerts: StateFlow<List<Alert>> = alertRepository.getRecentAlerts(10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isMonitoring: StateFlow<Boolean> = MonitoringService.isRunning

    private val _monitoringStartTime = MutableStateFlow(0L)
    val monitoringStartTime: StateFlow<Long> = _monitoringStartTime.asStateFlow()

    private val _isDemoMode = MutableStateFlow(false)
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

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
