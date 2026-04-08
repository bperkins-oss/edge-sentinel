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

package com.bp22intel.edgesentinel.mesh

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeshViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(MeshUiState())
    val uiState: StateFlow<MeshUiState> = _uiState.asStateFlow()

    private var meshService: MeshService? = null
    private var bound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val meshBinder = binder as? MeshService.MeshBinder ?: return
            meshService = meshBinder.service
            bound = true
            observeServiceState()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            meshService = null
            bound = false
        }
    }

    init {
        bindToService()
    }

    private fun bindToService() {
        val intent = Intent(context, MeshService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun observeServiceState() {
        val service = meshService ?: return

        viewModelScope.launch {
            service.meshState.collect { state ->
                _uiState.value = _uiState.value.copy(
                    isActive = state.isActive,
                    connectedPeerCount = state.connectedPeerCount,
                    totalAlertsReceived = state.totalAlertsReceived,
                    corroboratedAlertCount = state.corroboratedAlertCount
                )
            }
        }

        viewModelScope.launch {
            service.aggregator.meshAlerts.collect { alerts ->
                _uiState.value = _uiState.value.copy(
                    recentMeshAlerts = alerts.takeLast(50).reversed()
                )
            }
        }

        viewModelScope.launch {
            service.aggregator.correlatedAlerts.collect { correlated ->
                _uiState.value = _uiState.value.copy(
                    correlatedAlerts = correlated
                )
            }
        }
    }

    fun startMesh() {
        MeshService.start(context)
        if (!bound) bindToService()
    }

    fun stopMesh() {
        MeshService.stop(context)
        _uiState.value = _uiState.value.copy(isActive = false)
    }

    override fun onCleared() {
        if (bound) {
            context.unbindService(connection)
            bound = false
        }
        super.onCleared()
    }
}

data class MeshUiState(
    val isActive: Boolean = false,
    val connectedPeerCount: Int = 0,
    val totalAlertsReceived: Int = 0,
    val corroboratedAlertCount: Int = 0,
    val recentMeshAlerts: List<MeshAlert> = emptyList(),
    val correlatedAlerts: List<MeshAlertAggregator.CorrelatedAlert> = emptyList()
)
