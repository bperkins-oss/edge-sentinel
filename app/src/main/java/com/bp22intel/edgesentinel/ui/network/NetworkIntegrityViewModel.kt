/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024-2026 BP22 Intel. All Rights Reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use of this software, in whole or in
 * part, is strictly prohibited without prior written permission from
 * BP22 Intel.
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
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val captivePortalDetector: CaptivePortalDetector,
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        private const val PREFS_NAME = "trusted_mitm_services"
        private const val KEY_TRUSTED = "trusted_endpoints"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val vpnStatus = vpnMonitor.vpnStatus

    private val _snapshot = MutableStateFlow<NetworkIntegritySnapshot?>(null)
    val snapshot: StateFlow<NetworkIntegritySnapshot?> = _snapshot.asStateFlow()

    private val _trustedMitmServices = MutableStateFlow<Set<String>>(emptySet())
    val trustedMitmServices: StateFlow<Set<String>> = _trustedMitmServices.asStateFlow()

    private val _checkHistory = MutableStateFlow<List<NetworkIntegritySnapshot>>(emptyList())
    val checkHistory: StateFlow<List<NetworkIntegritySnapshot>> = _checkHistory.asStateFlow()

    private val _isChecking = MutableStateFlow(false)
    val isChecking: StateFlow<Boolean> = _isChecking.asStateFlow()

    init {
        vpnMonitor.startMonitoring()
        _trustedMitmServices.value = prefs.getStringSet(KEY_TRUSTED, emptySet()) ?: emptySet()
    }

    fun trustMitmService(endpoint: String) {
        val updated = _trustedMitmServices.value + endpoint
        _trustedMitmServices.value = updated
        prefs.edit().putStringSet(KEY_TRUSTED, updated).apply()
    }

    fun untrustMitmService(endpoint: String) {
        val updated = _trustedMitmServices.value - endpoint
        _trustedMitmServices.value = updated
        prefs.edit().putStringSet(KEY_TRUSTED, updated).apply()
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
                val untrustedMitm = tlsResult.mitmEndpoints.filter { it !in _trustedMitmServices.value }
                if (untrustedMitm.isNotEmpty()) score -= 30
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
