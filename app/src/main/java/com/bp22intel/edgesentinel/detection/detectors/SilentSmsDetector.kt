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

package com.bp22intel.edgesentinel.detection.detectors

import com.bp22intel.edgesentinel.domain.model.CellTower
import com.bp22intel.edgesentinel.domain.model.Confidence
import com.bp22intel.edgesentinel.domain.model.DetectionResult
import com.bp22intel.edgesentinel.domain.model.ThreatType
import javax.inject.Inject

/**
 * Detects Type-0 (silent/stealth) SMS messages used for device tracking.
 *
 * Silent SMS messages are sent by IMSI catchers and law enforcement to ping
 * a device's location without the user's knowledge. Detection of these messages
 * requires either root access or a custom baseband, neither of which is available
 * in a standard Android app.
 *
 * This detector is a stub. Without root access, it always returns null.
 * It can be triggered in demo mode to show what a detection would look like.
 */
class SilentSmsDetector @Inject constructor() : ThreatDetector {

    override val type: ThreatType = ThreatType.SILENT_SMS

    /**
     * Whether demo mode is active. When true, the detector will generate
     * a synthetic detection result for UI testing.
     */
    var demoMode: Boolean = false

    override suspend fun analyze(
        cells: List<CellTower>,
        history: List<CellTower>
    ): DetectionResult? {
        // In demo mode, return a synthetic detection for UI testing
        if (demoMode) {
            return createDemoResult()
        }

        // Without root access, we cannot intercept Type-0 SMS at the baseband level.
        // The Android SMS API does not surface silent/stealth messages.
        // TODO: Investigate BroadcastReceiver for SMS_RECEIVED with PID checking
        //       (may catch some Type-0 on certain OEMs that leak them)
        return null
    }

    private fun createDemoResult(): DetectionResult {
        return DetectionResult(
            threatType = type,
            score = 4.0,
            confidence = Confidence.MEDIUM,
            summary = "Silent SMS (Type-0) detected — device may be under location tracking",
            details = mapOf(
                "type" to "Type-0 SMS (silent ping)",
                "protocol_id" to "0x40",
                "source" to "Demo mode — real detection requires root access",
                "mitigation" to "Consider enabling airplane mode if under active surveillance"
            )
        )
    }
}
