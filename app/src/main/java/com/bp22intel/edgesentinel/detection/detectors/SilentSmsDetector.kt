/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024-2026 BP22 Intel. All Rights Reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use of this software, in whole or in
 * part, is strictly prohibited without prior written permission from
 * BP22 Intel.
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
