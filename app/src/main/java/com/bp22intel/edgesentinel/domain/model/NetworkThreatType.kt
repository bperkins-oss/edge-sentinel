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

package com.bp22intel.edgesentinel.domain.model

enum class NetworkThreatType(val label: String, val description: String) {
    VPN_DROP(
        "VPN Drop",
        "VPN connection was silently dropped without user action"
    ),
    DNS_HIJACK(
        "DNS Hijack",
        "DNS responses from device resolver differ from trusted resolvers"
    ),
    TLS_MITM(
        "TLS MITM",
        "TLS certificate chain indicates man-in-the-middle interception"
    ),
    CAPTIVE_PORTAL_INJECT(
        "Captive Portal Injection",
        "Captive portal detected with potential JavaScript injection"
    ),
    DNS_NXDOMAIN_HIJACK(
        "NXDOMAIN Hijack",
        "ISP/state actor redirecting failed DNS lookups to controlled servers"
    )
}
