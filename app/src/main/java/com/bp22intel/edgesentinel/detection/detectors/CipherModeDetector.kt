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
 * Real IMSI catchers often force A5/0 (no encryption) or A5/1 (weak encryption)
 * instead of the stronger A5/3. Detecting this requires reading the Cipher Mode
 * Command from the baseband processor via the Qualcomm DIAG port (/dev/diag).
 *
 * On rooted devices with a Qualcomm baseband, this delegates to [DiagBasedDetector]
 * which reads actual cipher mode commands from /dev/diag.
 *
 * On non-rooted devices, returns null (no detection possible — the Android
 * Telephony API does not expose cipher algorithm information).
 *
 * TODO: Consider Samsung Shannon baseband interface as alternative to Qualcomm DIAG
 * TODO: Investigate MediaTek engineering mode for cipher info on MTK devices
 */
class CipherModeDetector @Inject constructor(
    private val diagBasedDetector: DiagBasedDetector,
) : ThreatDetector {

    override val type: ThreatType = ThreatType.CIPHER_ANOMALY

    override suspend fun analyze(
        cells: List<CellTower>,
        history: List<CellTower>
    ): DetectionResult? {
        // Delegate to the DIAG-based detector which handles root checking internally.
        // Returns null on non-rooted devices (graceful degradation).
        return diagBasedDetector.analyze(cells, history)
    }
}
