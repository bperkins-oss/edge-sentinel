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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.domain.model.SensorCategory
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import com.bp22intel.edgesentinel.domain.model.ThreatType
import com.bp22intel.edgesentinel.domain.repository.AlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

enum class AlertFilter {
    ALL, SUSPICIOUS, THREAT
}

enum class CategoryFilter {
    ALL, CELLULAR, WIFI, BLUETOOTH, NETWORK, BASELINE
}

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val alertRepository: AlertRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(AlertFilter.ALL)
    val selectedFilter: StateFlow<AlertFilter> = _selectedFilter.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow(CategoryFilter.ALL)
    val selectedCategoryFilter: StateFlow<CategoryFilter> = _selectedCategoryFilter.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val alerts: StateFlow<List<Alert>> = combine(
        alertRepository.getAllAlerts(),
        _selectedFilter,
        _selectedCategoryFilter
    ) { allAlerts, filter, categoryFilter ->
        var filteredAlerts = allAlerts
        
        // Apply severity filter
        filteredAlerts = when (filter) {
            AlertFilter.ALL -> filteredAlerts
            AlertFilter.SUSPICIOUS -> filteredAlerts.filter { it.severity == ThreatLevel.SUSPICIOUS }
            AlertFilter.THREAT -> filteredAlerts.filter { it.severity == ThreatLevel.THREAT }
        }
        
        // Apply category filter
        filteredAlerts = when (categoryFilter) {
            CategoryFilter.ALL -> filteredAlerts
            CategoryFilter.CELLULAR -> filteredAlerts.filter { threatTypeToSensorCategory(it.threatType) == SensorCategory.CELLULAR }
            CategoryFilter.WIFI -> filteredAlerts.filter { threatTypeToSensorCategory(it.threatType) == SensorCategory.WIFI }
            CategoryFilter.BLUETOOTH -> filteredAlerts.filter { threatTypeToSensorCategory(it.threatType) == SensorCategory.BLUETOOTH }
            CategoryFilter.NETWORK -> filteredAlerts.filter { threatTypeToSensorCategory(it.threatType) == SensorCategory.NETWORK }
            CategoryFilter.BASELINE -> filteredAlerts.filter { threatTypeToSensorCategory(it.threatType) == SensorCategory.BASELINE }
        }
        
        filteredAlerts
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(filter: AlertFilter) {
        _selectedFilter.value = filter
    }

    fun setCategoryFilter(filter: CategoryFilter) {
        _selectedCategoryFilter.value = filter
    }

    fun acknowledgeAlert(id: Long) {
        viewModelScope.launch {
            alertRepository.acknowledgeAlert(id)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                // Simulate refresh delay - in a real app, this might trigger
                // a fresh scan or reload from backend
                delay(1000)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun threatTypeToSensorCategory(threatType: ThreatType): SensorCategory {
        return when (threatType) {
            ThreatType.FAKE_BTS,
            ThreatType.NETWORK_DOWNGRADE,
            ThreatType.SILENT_SMS,
            ThreatType.TRACKING_PATTERN,
            ThreatType.CIPHER_ANOMALY,
            ThreatType.SIGNAL_ANOMALY,
            ThreatType.NR_ANOMALY -> SensorCategory.CELLULAR
        }
    }
}
