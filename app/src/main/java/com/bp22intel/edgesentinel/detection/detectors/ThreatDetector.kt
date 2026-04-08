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

/**
 * Common interface for all threat detection modules.
 *
 * Each detector focuses on a specific class of cellular threat and produces
 * a [DetectionResult] when indicators are found, or null when nothing is detected.
 */
interface ThreatDetector {
    /** The category of threat this detector looks for. */
    val type: ThreatType

    /**
     * Analyze current cell towers against historical data for threat indicators.
     *
     * @param cells Currently visible cell towers.
     * @param history Previously observed cell towers for baseline comparison.
     * @return A [DetectionResult] if indicators are found, or null if clean.
     */
    suspend fun analyze(cells: List<CellTower>, history: List<CellTower>): DetectionResult?
}
