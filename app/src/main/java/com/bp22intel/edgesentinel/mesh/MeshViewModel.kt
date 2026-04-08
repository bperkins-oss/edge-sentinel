/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024 BP22 Intel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.bp22intel.edgesentinel.mesh

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private const val TAG = "MeshViewModel"

/**
 * Mesh networking ViewModel.
 * Currently runs in demo/preview mode — toggles UI state and shows
 * mock peer data. Real BLE discovery will be enabled in a future build
 * once BLE permission handling is fully tested across Android versions.
 */
@HiltViewModel
class MeshViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(MeshUiState())
    val uiState: StateFlow<MeshUiState> = _uiState.asStateFlow()

    fun startMesh() {
        Log.d(TAG, "Mesh enabled (demo mode)")
        _uiState.value = _uiState.value.copy(
            isActive = true,
            error = null
        )
    }

    fun stopMesh() {
        Log.d(TAG, "Mesh disabled")
        _uiState.value = _uiState.value.copy(isActive = false)
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class MeshUiState(
    val isActive: Boolean = false,
    val connectedPeerCount: Int = 0,
    val totalAlertsReceived: Int = 0,
    val corroboratedAlertCount: Int = 0,
    val recentMeshAlerts: List<MeshAlert> = emptyList(),
    val correlatedAlerts: List<MeshAlertAggregator.CorrelatedAlert> = emptyList(),
    val error: String? = null
)
