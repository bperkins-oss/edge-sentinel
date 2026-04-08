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

package com.bp22intel.edgesentinel.detection.network

import com.bp22intel.edgesentinel.domain.model.NetworkThreatType

/**
 * Result of a VPN status check.
 */
data class VpnStatusResult(
    val timestamp: Long = System.currentTimeMillis(),
    val isVpnActive: Boolean,
    val wasVpnActive: Boolean,
    val vpnDropDetected: Boolean,
    val vpnUptimeMs: Long = 0L,
    val vpnDowntimeMs: Long = 0L,
    val lastDropTimestamp: Long? = null,
    val bypassLeakDetected: Boolean = false,
    val leakDetails: String? = null
)

/**
 * Result of querying a single DNS resolver.
 */
data class DnsResolverResult(
    val resolverAddress: String,
    val resolverName: String,
    val resolvedAddresses: List<String>,
    val queryTimeMs: Long,
    val error: String? = null
)

/**
 * Result of a DNS integrity check for a single domain.
 */
data class DnsCheckResult(
    val domain: String,
    val timestamp: Long = System.currentTimeMillis(),
    val deviceResult: DnsResolverResult,
    val trustedResults: List<DnsResolverResult>,
    val hijackDetected: Boolean,
    val nxdomainHijacked: Boolean = false,
    val mismatchDetails: String? = null
)

/**
 * Aggregate result of all DNS integrity checks.
 */
data class DnsIntegrityResult(
    val timestamp: Long = System.currentTimeMillis(),
    val domainResults: List<DnsCheckResult>,
    val overallClean: Boolean,
    val hijackedDomains: List<String> = emptyList(),
    val nxdomainHijacked: Boolean = false
)

/**
 * Result of a TLS integrity check for a single endpoint.
 */
data class TlsCheckResult(
    val hostname: String,
    val timestamp: Long = System.currentTimeMillis(),
    val certificateChainValid: Boolean,
    val mitmDetected: Boolean,
    val presentedIssuer: String? = null,
    val expectedIssuer: String? = null,
    val certificateFingerprint: String? = null,
    val fingerprintMatch: Boolean? = null,
    val error: String? = null
)

/**
 * Aggregate result of all TLS integrity checks.
 */
data class TlsIntegrityResult(
    val timestamp: Long = System.currentTimeMillis(),
    val endpointResults: List<TlsCheckResult>,
    val overallClean: Boolean,
    val mitmEndpoints: List<String> = emptyList()
)

/**
 * Result of a captive portal detection check.
 */
data class CaptivePortalResult(
    val timestamp: Long = System.currentTimeMillis(),
    val captivePortalDetected: Boolean,
    val portalUrl: String? = null,
    val jsInjectionDetected: Boolean = false,
    val injectionDetails: String? = null,
    val httpStatusCode: Int? = null
)

/**
 * Composite result of all network integrity checks.
 */
data class NetworkIntegritySnapshot(
    val timestamp: Long = System.currentTimeMillis(),
    val vpnStatus: VpnStatusResult? = null,
    val dnsIntegrity: DnsIntegrityResult? = null,
    val tlsIntegrity: TlsIntegrityResult? = null,
    val captivePortal: CaptivePortalResult? = null,
    val trustScore: Int = 100,
    val threats: List<NetworkThreatType> = emptyList()
) {
    val isClean: Boolean
        get() = threats.isEmpty()
}
