/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024-2026 BP22 Intel. All Rights Reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use of this software, in whole or in
 * part, is strictly prohibited without prior written permission from
 * BP22 Intel.
 */

package com.bp22intel.edgesentinel.ui.alerts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp22intel.edgesentinel.analysis.AlertAnalysis
import com.bp22intel.edgesentinel.analysis.ThreatAnalyst
import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.domain.repository.AlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlertDetailUiState(
    val alert: Alert? = null,
    val analysis: AlertAnalysis? = null,
    val isLoading: Boolean = true,
    val isAcknowledged: Boolean = false
)

@HiltViewModel
class AlertDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val alertRepository: AlertRepository,
    private val threatAnalyst: ThreatAnalyst
) : ViewModel() {

    private val alertId: Long = savedStateHandle["alertId"] ?: 0L

    private val _uiState = MutableStateFlow(AlertDetailUiState())
    val uiState: StateFlow<AlertDetailUiState> = _uiState.asStateFlow()

    init {
        loadAlert()
    }

    private fun loadAlert() {
        viewModelScope.launch {
            val alert = alertRepository.getAlertById(alertId)
            if (alert != null) {
                val analysis = threatAnalyst.analyzeAlert(alert)
                _uiState.value = AlertDetailUiState(
                    alert = alert,
                    analysis = analysis,
                    isLoading = false,
                    isAcknowledged = alert.acknowledged
                )
            } else {
                _uiState.value = AlertDetailUiState(isLoading = false)
            }
        }
    }

    fun acknowledgeAlert() {
        viewModelScope.launch {
            alertRepository.acknowledgeAlert(alertId)
            _uiState.value = _uiState.value.copy(isAcknowledged = true)
        }
    }
}
