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

package com.bp22intel.edgesentinel.ui.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp22intel.edgesentinel.detection.network.CaptivePortalDetector
import com.bp22intel.edgesentinel.detection.network.DnsIntegrityChecker
import com.bp22intel.edgesentinel.detection.network.NetworkIntegritySnapshot
import com.bp22intel.edgesentinel.detection.network.TlsIntegrityChecker
import com.bp22intel.edgesentinel.detection.network.VpnMonitor
import com.bp22intel.edgesentinel.domain.model.NetworkThreatType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkIntegrityViewModel @Inject constructor(
    private val vpnMonitor: VpnMonitor,
    private val dnsChecker: DnsIntegrityChecker,
    private val tlsChecker: TlsIntegrityChecker,
    private val captivePortalDetector: CaptivePortalDetector
) : ViewModel() {

    val vpnStatus = vpnMonitor.vpnStatus

    private val _snapshot = MutableStateFlow<NetworkIntegritySnapshot?>(null)
    val snapshot: StateFlow<NetworkIntegritySnapshot?> = _snapshot.asStateFlow()

    private val _checkHistory = MutableStateFlow<List<NetworkIntegritySnapshot>>(emptyList())
    val checkHistory: StateFlow<List<NetworkIntegritySnapshot>> = _checkHistory.asStateFlow()

    private val _isChecking = MutableStateFlow(false)
    val isChecking: StateFlow<Boolean> = _isChecking.asStateFlow()

    init {
        vpnMonitor.startMonitoring()
    }

    /**
     * Runs all network integrity checks concurrently and produces a composite snapshot.
     */
    fun runFullCheck() {
        if (_isChecking.value) return

        viewModelScope.launch {
            _isChecking.value = true
            try {
                val vpnResult = vpnMonitor.refreshVpnState()
                val dnsDeferred = async { dnsChecker.runFullCheck() }
                val tlsDeferred = async { tlsChecker.runFullCheck() }
                val portalDeferred = async { captivePortalDetector.runCheck() }

                val dnsResult = dnsDeferred.await()
                val tlsResult = tlsDeferred.await()
                val portalResult = portalDeferred.await()

                // Build threat list
                val threats = buildList {
                    if (vpnResult.vpnDropDetected) add(NetworkThreatType.VPN_DROP)
                    if (dnsResult.hijackedDomains.isNotEmpty()) add(NetworkThreatType.DNS_HIJACK)
                    if (dnsResult.nxdomainHijacked) add(NetworkThreatType.DNS_NXDOMAIN_HIJACK)
                    if (tlsResult.mitmEndpoints.isNotEmpty()) add(NetworkThreatType.TLS_MITM)
                    if (portalResult.jsInjectionDetected) add(NetworkThreatType.CAPTIVE_PORTAL_INJECT)
                }

                // Compute trust score (start at 100, deduct per threat type)
                var score = 100
                if (vpnResult.vpnDropDetected) score -= 20
                if (vpnResult.bypassLeakDetected) score -= 15
                if (dnsResult.hijackedDomains.isNotEmpty()) score -= 25
                if (dnsResult.nxdomainHijacked) score -= 10
                if (tlsResult.mitmEndpoints.isNotEmpty()) score -= 30
                if (portalResult.captivePortalDetected) score -= 10
                if (portalResult.jsInjectionDetected) score -= 15

                val snapshot = NetworkIntegritySnapshot(
                    vpnStatus = vpnResult,
                    dnsIntegrity = dnsResult,
                    tlsIntegrity = tlsResult,
                    captivePortal = portalResult,
                    trustScore = score.coerceIn(0, 100),
                    threats = threats
                )

                _snapshot.value = snapshot
                _checkHistory.value = (listOf(snapshot) + _checkHistory.value).take(50)
            } finally {
                _isChecking.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        vpnMonitor.stopMonitoring()
    }
}
