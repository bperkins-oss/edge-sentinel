/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024-2026 BP22 Intel. All Rights Reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use of this software, in whole or in
 * part, is strictly prohibited without prior written permission from
 * BP22 Intel.
 */

package com.bp22intel.edgesentinel.ui.alerts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.domain.model.ThreatType
import com.bp22intel.edgesentinel.domain.repository.AlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertDetailViewModel @Inject constructor(
    private val alertRepository: AlertRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val alertId: Long = savedStateHandle.get<Long>("alertId") ?: 0L

    private val _alert = MutableStateFlow<Alert?>(null)
    val alert: StateFlow<Alert?> = _alert.asStateFlow()

    init {
        loadAlert()
    }

    private fun loadAlert() {
        viewModelScope.launch {
            _alert.value = alertRepository.getAlertById(alertId)
        }
    }

    fun getRecommendedAction(threatType: ThreatType): String {
        return when (threatType) {
            ThreatType.FAKE_BTS -> "Move away from your current location if possible. " +
                "Avoid making sensitive calls or sending confidential messages. " +
                "Consider enabling airplane mode temporarily and switching to Wi-Fi calling."

            ThreatType.NETWORK_DOWNGRADE -> "Your connection has been forced to a less secure " +
                "network protocol. Avoid transmitting sensitive data. If this persists, " +
                "it may indicate an active interception attempt. Move to a different area."

            ThreatType.SILENT_SMS -> "Your device received a silent SMS, which is commonly " +
                "used for location tracking. This is often used by law enforcement or " +
                "surveillance operations. Consider powering off your device if you need privacy."

            ThreatType.TRACKING_PATTERN -> "Unusual cell tower activity suggests your device's " +
                "location may be actively tracked. Consider enabling airplane mode and " +
                "moving to a different location before re-enabling cellular connectivity."

            ThreatType.CIPHER_ANOMALY -> "The encryption on your cellular connection appears " +
                "weakened or disabled. Do not make sensitive calls or send confidential data. " +
                "Switch to encrypted messaging apps (Signal, etc.) for communication."

            ThreatType.SIGNAL_ANOMALY -> "An unusual signal pattern was detected but may be " +
                "benign. Continue monitoring and check if the pattern persists. No immediate " +
                "action is required unless other threat indicators are also present."

            ThreatType.NR_ANOMALY -> "A 5G NR anomaly was detected — this may indicate " +
                "a rogue gNodeB, NR bidding-down attack, or suspicious NR cell behavior. " +
                "Monitor for correlated alerts. If persistent, avoid sensitive communications."
        }
    }
}
