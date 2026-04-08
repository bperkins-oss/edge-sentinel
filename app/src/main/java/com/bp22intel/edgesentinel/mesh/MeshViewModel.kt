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

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.ParcelUuid
import android.util.Log
import androidx.core.content.ContextCompat
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

@HiltViewModel
class MeshViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        // Valid hex UUIDs for Edge Sentinel mesh
        val MESH_SERVICE_UUID: UUID = UUID.fromString("ed9e5e71-1ae1-4d3a-b5c7-ae5b00000001")
    }

    private val _uiState = MutableStateFlow(MeshUiState())
    val uiState: StateFlow<MeshUiState> = _uiState.asStateFlow()

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        try {
            (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
        } catch (e: Exception) {
            Log.w(TAG, "Could not get BluetoothAdapter: ${e.message}")
            null
        }
    }

    private var scanner: BluetoothLeScanner? = null
    private var isScanning = false
    private var maintenanceJob: Job? = null
    private val discoveredPeers = mutableMapOf<String, PeerInfo>()

    data class PeerInfo(
        val deviceAddress: String,
        val rssi: Int,
        val lastSeen: Long,
        val deviceName: String?
    )

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            try {
                result ?: return
                val address = result.device?.address ?: return
                val rssi = result.rssi
                val name = try { result.device?.name } catch (_: SecurityException) { null }

                discoveredPeers[address] = PeerInfo(
                    deviceAddress = address,
                    rssi = rssi,
                    lastSeen = System.currentTimeMillis(),
                    deviceName = name
                )

                _uiState.value = _uiState.value.copy(
                    connectedPeerCount = discoveredPeers.size
                )

                Log.d(TAG, "Peer found: ${address.takeLast(5)} RSSI: $rssi")
            } catch (e: Exception) {
                Log.w(TAG, "Error in scan callback: ${e.message}")
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.w(TAG, "BLE scan failed with error code: $errorCode")
            val errorMsg = when (errorCode) {
                SCAN_FAILED_ALREADY_STARTED -> "Scan already running"
                SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "App registration failed"
                SCAN_FAILED_FEATURE_UNSUPPORTED -> "BLE scanning not supported on this device"
                SCAN_FAILED_INTERNAL_ERROR -> "Internal BLE error"
                else -> "Unknown error ($errorCode)"
            }
            _uiState.value = _uiState.value.copy(error = "Scan failed: $errorMsg")
        }
    }

    fun startMesh() {
        if (_uiState.value.isActive) return

        // Check Bluetooth availability
        val adapter = bluetoothAdapter
        if (adapter == null) {
            _uiState.value = _uiState.value.copy(
                error = "Bluetooth not available on this device"
            )
            return
        }

        if (!adapter.isEnabled) {
            _uiState.value = _uiState.value.copy(
                error = "Please enable Bluetooth first"
            )
            return
        }

        // Check permissions
        if (!hasBlePermissions()) {
            _uiState.value = _uiState.value.copy(
                error = "Bluetooth permissions not granted"
            )
            return
        }

        // Start scanning
        try {
            startBleScan(adapter)
            _uiState.value = _uiState.value.copy(isActive = true, error = null)
            Log.d(TAG, "Mesh started — scanning for peers")

            // Maintenance: prune stale peers every 30s
            maintenanceJob = viewModelScope.launch {
                while (isActive) {
                    delay(30_000)
                    pruneStale()
                }
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "Security exception starting BLE: ${e.message}")
            _uiState.value = _uiState.value.copy(
                error = "Bluetooth permission denied by system"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start mesh: ${e.message}", e)
            _uiState.value = _uiState.value.copy(
                error = "Could not start: ${e.message}"
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun startBleScan(adapter: BluetoothAdapter) {
        scanner = adapter.bluetoothLeScanner
        if (scanner == null) {
            _uiState.value = _uiState.value.copy(
                error = "BLE scanner not available — is Bluetooth on?"
            )
            return
        }

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .setReportDelay(0)
            .build()

        // Scan for all BLE devices (no filter) so we find Edge Sentinel peers
        // In production, filter by MESH_SERVICE_UUID
        scanner?.startScan(null, settings, scanCallback)
        isScanning = true
    }

    @SuppressLint("MissingPermission")
    fun stopMesh() {
        maintenanceJob?.cancel()

        if (isScanning) {
            try {
                scanner?.stopScan(scanCallback)
            } catch (e: Exception) {
                Log.w(TAG, "Error stopping scan: ${e.message}")
            }
            isScanning = false
        }

        scanner = null
        discoveredPeers.clear()
        _uiState.value = _uiState.value.copy(
            isActive = false,
            connectedPeerCount = 0,
            error = null
        )
        Log.d(TAG, "Mesh stopped")
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun pruneStale() {
        val cutoff = System.currentTimeMillis() - 120_000 // 2 minutes
        val before = discoveredPeers.size
        discoveredPeers.entries.removeAll { it.value.lastSeen < cutoff }
        if (discoveredPeers.size != before) {
            _uiState.value = _uiState.value.copy(
                connectedPeerCount = discoveredPeers.size
            )
        }
    }

    private fun hasBlePermissions(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
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
