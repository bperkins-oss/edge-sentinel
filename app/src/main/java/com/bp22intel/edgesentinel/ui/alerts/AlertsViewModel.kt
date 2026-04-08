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
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import com.bp22intel.edgesentinel.domain.repository.AlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AlertFilter {
    ALL, SUSPICIOUS, THREAT
}

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val alertRepository: AlertRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(AlertFilter.ALL)
    val selectedFilter: StateFlow<AlertFilter> = _selectedFilter.asStateFlow()

    val alerts: StateFlow<List<Alert>> = combine(
        alertRepository.getAllAlerts(),
        _selectedFilter
    ) { allAlerts, filter ->
        when (filter) {
            AlertFilter.ALL -> allAlerts
            AlertFilter.SUSPICIOUS -> allAlerts.filter { it.severity == ThreatLevel.SUSPICIOUS }
            AlertFilter.THREAT -> allAlerts.filter { it.severity == ThreatLevel.THREAT }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(filter: AlertFilter) {
        _selectedFilter.value = filter
    }

    fun acknowledgeAlert(id: Long) {
        viewModelScope.launch {
            alertRepository.acknowledgeAlert(id)
        }
    }
}
