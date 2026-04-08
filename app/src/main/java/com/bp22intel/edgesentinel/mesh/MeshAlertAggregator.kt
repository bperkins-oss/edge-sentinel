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

package com.bp22intel.edgesentinel.mesh

import com.bp22intel.edgesentinel.domain.model.Confidence
import com.bp22intel.edgesentinel.domain.model.ThreatType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Aggregates alerts from mesh peers and correlates them with local detections.
 *
 * Correlation logic: when multiple devices detect the same threat type on the
 * same cell within a time window, confidence is boosted. For example, if 3 out
 * of 5 devices in a convoy detect a network downgrade simultaneously, confidence
 * goes from MEDIUM to HIGH.
 */
class MeshAlertAggregator(private val localDeviceId: String) {

    companion object {
        /** Time window for correlating alerts from different devices. */
        private const val CORRELATION_WINDOW_MS = 60_000L // 60 seconds
        /** Minimum peers with same alert to boost confidence. */
        private const val MIN_PEERS_FOR_BOOST = 2
        /** Max alerts to retain in the aggregation buffer. */
        private const val MAX_BUFFER_SIZE = 200
    }

    /** A correlated alert group — same threat type and cell within the time window. */
    data class CorrelatedAlert(
        val threatType: ThreatType,
        val cellCid: Int?,
        val firstSeen: Long,
        val alerts: List<MeshAlert>,
        val boostedConfidence: Confidence
    ) {
        val peerCount: Int get() = alerts.map { it.deviceId }.distinct().size
        val isCorroborated: Boolean get() = peerCount >= MIN_PEERS_FOR_BOOST
    }

    private val _meshAlerts = MutableStateFlow<List<MeshAlert>>(emptyList())
    val meshAlerts: StateFlow<List<MeshAlert>> = _meshAlerts.asStateFlow()

    private val _correlatedAlerts = MutableStateFlow<List<CorrelatedAlert>>(emptyList())
    val correlatedAlerts: StateFlow<List<CorrelatedAlert>> = _correlatedAlerts.asStateFlow()

    // Track message IDs to avoid duplicates
    private val seenMessageIds = linkedSetOf<String>()

    /** Ingest a mesh alert from a peer device. */
    fun onAlertReceived(alert: MeshAlert) {
        // Ignore our own alerts bounced back and duplicates
        if (alert.deviceId == localDeviceId) return
        if (!seenMessageIds.add(alert.messageId)) return

        // Cap the seen set
        while (seenMessageIds.size > MAX_BUFFER_SIZE * 2) {
            seenMessageIds.remove(seenMessageIds.first())
        }

        val current = _meshAlerts.value.toMutableList()
        current.add(alert)

        // Cap buffer size
        while (current.size > MAX_BUFFER_SIZE) {
            current.removeFirst()
        }

        _meshAlerts.value = current
        recorrelate()
    }

    /** Correlate a local detection with mesh alerts to boost confidence. */
    fun correlateLocal(
        threatType: ThreatType,
        confidence: Confidence,
        cellCid: Int?
    ): Confidence {
        val now = System.currentTimeMillis()
        val matchingPeers = _meshAlerts.value
            .filter { it.threatType == threatType }
            .filter { now - it.timestamp < CORRELATION_WINDOW_MS }
            .filter { cellCid == null || it.cellCid == null || it.cellCid == cellCid }
            .map { it.deviceId }
            .distinct()
            .size

        return boostConfidence(confidence, matchingPeers)
    }

    private fun recorrelate() {
        val now = System.currentTimeMillis()
        val recent = _meshAlerts.value.filter { now - it.timestamp < CORRELATION_WINDOW_MS }

        // Group by (threatType, cellCid)
        val groups = recent.groupBy { it.threatType to it.cellCid }

        _correlatedAlerts.value = groups.map { (key, alerts) ->
            val (threatType, cellCid) = key
            val peerCount = alerts.map { it.deviceId }.distinct().size
            val maxConfidence = alerts.maxOf { it.confidence }
            CorrelatedAlert(
                threatType = threatType,
                cellCid = cellCid,
                firstSeen = alerts.minOf { it.timestamp },
                alerts = alerts,
                boostedConfidence = boostConfidence(maxConfidence, peerCount)
            )
        }.sortedByDescending { it.firstSeen }
    }

    private fun boostConfidence(base: Confidence, corroboratingPeers: Int): Confidence {
        if (corroboratingPeers < MIN_PEERS_FOR_BOOST) return base
        return when (base) {
            Confidence.LOW -> if (corroboratingPeers >= 3) Confidence.HIGH else Confidence.MEDIUM
            Confidence.MEDIUM -> Confidence.HIGH
            Confidence.HIGH -> Confidence.HIGH
        }
    }

    /** Remove stale alerts outside the correlation window. */
    fun pruneStale() {
        val cutoff = System.currentTimeMillis() - CORRELATION_WINDOW_MS * 3
        _meshAlerts.value = _meshAlerts.value.filter { it.timestamp > cutoff }
        recorrelate()
    }
}
