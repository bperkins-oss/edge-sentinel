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

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

private const val TAG = "MeshViewModel"

/**
 * Mesh networking ViewModel — runs BLE discovery directly without a foreground service.
 * The service-based approach crashed on multiple Android versions due to foreground service
 * permission requirements. This approach runs discovery only while the screen is active.
 */
@HiltViewModel
class MeshViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(MeshUiState())
    val uiState: StateFlow<MeshUiState> = _uiState.asStateFlow()

    private var discovery: MeshDiscovery? = null
    private var aggregator: MeshAlertAggregator? = null
    private var meshDeviceId: String = ""
    private var maintenanceJob: Job? = null
    private var alertCollectionJob: Job? = null

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
    }

    fun startMesh() {
        if (_uiState.value.isActive) return

        try {
            val adapter = bluetoothAdapter
            if (adapter == null || !adapter.isEnabled) {
                Log.w(TAG, "Bluetooth not available or not enabled")
                _uiState.value = _uiState.value.copy(
                    error = "Bluetooth is not enabled. Please enable Bluetooth and try again."
                )
                return
            }

            // Generate or retrieve device ID
            val prefs = context.getSharedPreferences("mesh_prefs", Context.MODE_PRIVATE)
            meshDeviceId = prefs.getString("mesh_device_id", null) ?: run {
                val newId = UUID.randomUUID().toString()
                prefs.edit().putString("mesh_device_id", newId).apply()
                newId
            }

            aggregator = MeshAlertAggregator(meshDeviceId)

            discovery = MeshDiscovery(
                context = context,
                deviceId = meshDeviceId,
                onPeerDiscovered = { peerId, rssi ->
                    Log.d(TAG, "Peer discovered: ${peerId.take(8)}... RSSI: $rssi")
                    updatePeerCount()
                },
                onMessageReceived = { _, data ->
                    handleIncomingMessage(data)
                }
            )

            discovery?.startDiscovery()
            _uiState.value = _uiState.value.copy(isActive = true, error = null)

            // Start maintenance loop
            maintenanceJob = viewModelScope.launch {
                while (isActive) {
                    delay(30_000)
                    discovery?.pruneStale(120_000)
                    aggregator?.pruneStale()
                    updatePeerCount()
                }
            }

            // Collect alerts
            alertCollectionJob = viewModelScope.launch {
                aggregator?.meshAlerts?.collect { alerts ->
                    _uiState.value = _uiState.value.copy(
                        recentMeshAlerts = alerts.takeLast(50).reversed()
                    )
                }
            }

            Log.d(TAG, "Mesh started (no-service mode) with device ID: ${meshDeviceId.take(8)}...")

        } catch (e: SecurityException) {
            Log.w(TAG, "BLE permissions not granted: ${e.message}")
            _uiState.value = _uiState.value.copy(
                error = "Bluetooth permissions required. Please grant permissions and try again."
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start mesh: ${e.message}", e)
            _uiState.value = _uiState.value.copy(
                error = "Could not start mesh: ${e.message}"
            )
        }
    }

    fun stopMesh() {
        maintenanceJob?.cancel()
        alertCollectionJob?.cancel()
        try {
            discovery?.stopDiscovery()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping discovery: ${e.message}")
        }
        discovery = null
        aggregator = null
        _uiState.value = _uiState.value.copy(isActive = false, error = null)
        Log.d(TAG, "Mesh stopped")
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun updatePeerCount() {
        val count = discovery?.discoveredPeers?.value?.size ?: 0
        _uiState.value = _uiState.value.copy(connectedPeerCount = count)
    }

    private fun handleIncomingMessage(data: ByteArray) {
        try {
            Log.d(TAG, "Received mesh message: ${data.size} bytes")
            _uiState.value = _uiState.value.copy(
                totalAlertsReceived = _uiState.value.totalAlertsReceived + 1
            )
        } catch (e: Exception) {
            Log.w(TAG, "Error processing mesh message: ${e.message}")
        }
    }

    override fun onCleared() {
        stopMesh()
        super.onCleared()
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
