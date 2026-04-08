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

/**
 * WiFi-specific threat categories detected by the WiFi analysis layer.
 */
enum class WifiThreatType(val label: String, val description: String) {
    EVIL_TWIN(
        "Evil Twin",
        "Multiple APs sharing the same SSID with different BSSIDs or conflicting security"
    ),
    ROGUE_AP(
        "Rogue AP",
        "New access point appearing at abnormally high signal strength"
    ),
    DEAUTH_ATTACK(
        "Deauth Attack",
        "Rapid connect/disconnect cycles indicating a deauthentication attack"
    ),
    SSID_SPOOF(
        "SSID Spoof",
        "Access point mimicking well-known network names in unusual locations"
    ),
    KARMA_ATTACK(
        "Karma Attack",
        "AP responding to probe requests with matching SSIDs — possible KARMA/MANA attack"
    ),
    PROBE_LEAK(
        "Probe Leak",
        "Device broadcasting probe requests that reveal previously-connected SSIDs"
    )
}
