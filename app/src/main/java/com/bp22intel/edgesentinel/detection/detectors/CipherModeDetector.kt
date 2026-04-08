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
import com.bp22intel.edgesentinel.domain.model.DetectionResult
import com.bp22intel.edgesentinel.domain.model.ThreatType
import javax.inject.Inject

/**
 * Detects cipher mode anomalies (encryption downgrade attacks).
 *
 * STUB IMPLEMENTATION — requires root access and Qualcomm DIAG interface.
 *
 * Real IMSI catchers often force A5/0 (no encryption) or A5/1 (weak encryption)
 * instead of the stronger A5/3. Detecting this requires reading the Cipher Mode
 * Command from the baseband processor, which is only accessible via:
 *
 * - Qualcomm DIAG port (/dev/diag) — requires root
 * - Custom baseband firmware
 * - OsmocomBB-compatible hardware
 *
 * Without root access, this detector always returns null.
 *
 * TODO: Future DIAG integration roadmap:
 *   1. Check for root access and /dev/diag availability
 *   2. Register DIAG log mask for GSM RR messages (0x512F)
 *   3. Parse Cipher Mode Command (message type 0x35)
 *   4. Extract cipher algorithm from IE and compare against baseline
 *   5. Alert on A5/0 or A5/1 when A5/3 was previously used
 *   6. Cross-reference with SnoopSnitch's cipher detection (k1/k2 coefficients)
 *
 * TODO: Consider Samsung Shannon baseband interface as alternative to Qualcomm DIAG
 * TODO: Investigate MediaTek engineering mode for cipher info on MTK devices
 */
class CipherModeDetector @Inject constructor() : ThreatDetector {

    override val type: ThreatType = ThreatType.CIPHER_ANOMALY

    override suspend fun analyze(
        cells: List<CellTower>,
        history: List<CellTower>
    ): DetectionResult? {
        // Cannot detect cipher mode anomalies without root access.
        // The Android Telephony API does not expose cipher algorithm information.
        return null
    }
}
