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

package com.bp22intel.edgesentinel.ui.baseline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp22intel.edgesentinel.baseline.BaselineAnomalyResult
import com.bp22intel.edgesentinel.baseline.BaselineConfidence
import com.bp22intel.edgesentinel.baseline.BaselineManager
import com.bp22intel.edgesentinel.baseline.LocationBaseline
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BaselineViewModel @Inject constructor(
    private val baselineManager: BaselineManager
) : ViewModel() {

    val baselines: StateFlow<List<LocationBaseline>> = baselineManager.getAllBaselines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentAnomaly = MutableStateFlow<BaselineAnomalyResult?>(null)
    val currentAnomaly: StateFlow<BaselineAnomalyResult?> = _currentAnomaly.asStateFlow()

    private val _selectedBaseline = MutableStateFlow<LocationBaseline?>(null)
    val selectedBaseline: StateFlow<LocationBaseline?> = _selectedBaseline.asStateFlow()

    fun selectBaseline(baseline: LocationBaseline) {
        _selectedBaseline.value = baseline
    }

    fun clearSelection() {
        _selectedBaseline.value = null
    }

    fun resetBaseline(baselineId: Long) {
        viewModelScope.launch {
            baselineManager.resetBaseline(baselineId)
            _selectedBaseline.value = null
        }
    }

    fun resetAll() {
        viewModelScope.launch {
            baselineManager.resetAll()
        }
    }
}
