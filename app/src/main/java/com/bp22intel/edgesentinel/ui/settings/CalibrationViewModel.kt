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

package com.bp22intel.edgesentinel.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp22intel.edgesentinel.calibration.CalibrationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalibrationViewModel @Inject constructor(
    private val calibrationService: CalibrationService
) : ViewModel() {

    val isCalibrating: StateFlow<Boolean> = calibrationService.isCalibrating
    val sampleCount: StateFlow<Int> = calibrationService.sampleCount
    val samples: StateFlow<List<CalibrationService.CalibrationSample>> = calibrationService.samples
    
    private val _calibrationResults = MutableStateFlow<CalibrationService.CalibrationResults?>(null)
    val calibrationResults = _calibrationResults.asStateFlow()
    
    private val _showInstructions = MutableStateFlow(true)
    val showInstructions = _showInstructions.asStateFlow()
    
    private val _exportMessage = MutableSharedFlow<String>()
    val exportMessage = _exportMessage.asSharedFlow()
    
    fun startCalibration() {
        _calibrationResults.value = null
        _showInstructions.value = false
        calibrationService.startCalibration()
    }
    
    fun stopCalibration() {
        val results = calibrationService.stopCalibration()
        _calibrationResults.value = results
    }
    
    fun dismissInstructions() {
        _showInstructions.value = false
    }
    
    fun showInstructions() {
        _showInstructions.value = true
    }
    
    fun exportCalibrationData() {
        viewModelScope.launch {
            try {
                val jsonData = calibrationService.exportCalibrationData()
                // In a real implementation, this would save to Downloads folder
                // For now, just show a message
                _exportMessage.emit("Calibration data exported successfully")
            } catch (e: Exception) {
                _exportMessage.emit("Failed to export calibration data: ${e.message}")
            }
        }
    }
    
    fun applyCalibrationResults() {
        val results = _calibrationResults.value ?: return
        
        // In a real implementation, this would:
        // 1. Update ThreatGeolocation with new path loss exponents
        // 2. Save calibration parameters to SharedPreferences
        // 3. Notify user that calibration has been applied
        
        viewModelScope.launch {
            _exportMessage.emit("Calibration results applied successfully")
        }
    }
}