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
import com.bp22intel.edgesentinel.analysis.AlertAnalysis
import com.bp22intel.edgesentinel.analysis.FilterRecommendation
import com.bp22intel.edgesentinel.analysis.ThreatAnalyst
import com.bp22intel.edgesentinel.data.local.dao.AlertFeedbackDao
import com.bp22intel.edgesentinel.data.local.dao.TrustedNetworkDao
import com.bp22intel.edgesentinel.data.local.entity.AlertFeedbackEntity
import com.bp22intel.edgesentinel.data.local.entity.TrustedNetworkEntity
import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.domain.repository.AlertRepository
import com.bp22intel.edgesentinel.fusion.SensorFusionEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

data class AlertDetailUiState(
    val alert: Alert? = null,
    val analysis: AlertAnalysis? = null,
    val filterRecommendation: FilterRecommendation? = null,
    val isLoading: Boolean = true,
    val isAcknowledged: Boolean = false,
    val feedbackGiven: String? = null,  // "FALSE_POSITIVE", "CONFIRMED_THREAT", "UNSURE", or null
    val feedbackConfirmation: String? = null  // Transient confirmation message
)

@HiltViewModel
class AlertDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val alertRepository: AlertRepository,
    private val threatAnalyst: ThreatAnalyst,
    private val feedbackDao: AlertFeedbackDao,
    private val trustedNetworkDao: TrustedNetworkDao,
    private val sensorFusionEngine: SensorFusionEngine
) : ViewModel() {

    private val alertId: Long = savedStateHandle["alertId"] ?: 0L

    private val _uiState = MutableStateFlow(AlertDetailUiState())
    val uiState: StateFlow<AlertDetailUiState> = _uiState.asStateFlow()

    init {
        loadAlert()
    }

    private fun loadAlert() {
        viewModelScope.launch {
            val alert = alertRepository.getAlertById(alertId)
            if (alert != null) {
                val (analysis, filterRec) = threatAnalyst.analyzeAlertWithLearning(alert)

                // Check if user already gave feedback on this alert.
                val existingFeedback = feedbackDao.getFeedbackForAlert(alertId)

                _uiState.value = AlertDetailUiState(
                    alert = alert,
                    analysis = analysis,
                    filterRecommendation = filterRec,
                    isLoading = false,
                    isAcknowledged = alert.acknowledged,
                    feedbackGiven = existingFeedback?.feedback
                )
            } else {
                _uiState.value = AlertDetailUiState(isLoading = false)
            }
        }
    }

    fun acknowledgeAlert() {
        viewModelScope.launch {
            alertRepository.acknowledgeAlert(alertId)
            _uiState.value = _uiState.value.copy(isAcknowledged = true)
        }
    }

    /**
     * Record user feedback for this alert.
     *
     * @param feedback One of "FALSE_POSITIVE", "CONFIRMED_THREAT", "KNOWN_DEVICE", "UNSURE".
     */
    fun submitFeedback(feedback: String) {
        val alert = _uiState.value.alert ?: return

        viewModelScope.launch {
            val details = try { JSONObject(alert.detailsJson) } catch (_: Exception) { JSONObject() }

            val ssid = details.optString("ssid", "").takeIf { it.isNotEmpty() }
            val bssid = details.optString("bssid", "").takeIf { it.isNotEmpty() }

            val entity = AlertFeedbackEntity(
                alertId = alert.id,
                threatType = alert.threatType.name,
                feedback = feedback,
                cellId = details.optLong("cellId", -1L).takeIf { it > 0 },
                lac = details.optInt("lac", -1).takeIf { it > 0 },
                mcc = details.optInt("mcc", -1).takeIf { it > 0 },
                mnc = details.optInt("mnc", -1).takeIf { it >= 0 },
                bssid = bssid,
                ssid = ssid,
                signalStrength = details.optInt("signalStrength", Int.MIN_VALUE)
                    .takeIf { it != Int.MIN_VALUE },
                latitude = details.optDouble("latitude", Double.NaN)
                    .takeIf { !it.isNaN() },
                longitude = details.optDouble("longitude", Double.NaN)
                    .takeIf { !it.isNaN() },
                timestamp = System.currentTimeMillis(),
                detailsSnapshot = alert.detailsJson
            )

            feedbackDao.insertFeedback(entity)

            // For KNOWN_DEVICE on WiFi alerts: trust the SSID (all APs broadcasting it)
            val isWifiAlert = ssid != null || bssid != null
            if (feedback == "KNOWN_DEVICE" && isWifiAlert && ssid != null) {
                // Add the SSID as a trusted network. Insert a representative entry
                // with the BSSID from this alert; the trust is SSID-level.
                trustedNetworkDao.insert(
                    TrustedNetworkEntity(
                        bssid = bssid ?: "ssid-trust:$ssid",
                        ssid = ssid,
                        label = "$ssid (trusted via alert feedback)"
                    )
                )
            }

            val confirmationMsg = when {
                feedback == "FALSE_POSITIVE" && isWifiAlert ->
                    "Got it. Future alerts for this network will be adjusted."
                feedback == "FALSE_POSITIVE" ->
                    "Got it. Future alerts from this tower will be adjusted."
                feedback == "CONFIRMED_THREAT" ->
                    "Confirmed. We'll stay extra alert for this threat type here."
                feedback == "KNOWN_DEVICE" && ssid != null ->
                    "\"$ssid\" and all its access points are now trusted. Future evil-twin alerts for this network will be suppressed."
                feedback == "KNOWN_DEVICE" ->
                    "Good catch! This tower is now marked as a known device (booster/femtocell). We'll suppress future alerts for this tower but the detection was correct."
                else -> "Noted. We'll keep monitoring."
            }

            _uiState.value = _uiState.value.copy(
                feedbackGiven = feedback,
                feedbackConfirmation = confirmationMsg
            )

            // Auto-acknowledge for FALSE_POSITIVE and KNOWN_DEVICE.
            if ((feedback == "FALSE_POSITIVE" || feedback == "KNOWN_DEVICE") && !_uiState.value.isAcknowledged) {
                alertRepository.acknowledgeAlert(alertId)
                _uiState.value = _uiState.value.copy(isAcknowledged = true)
            }

            // Dismiss the detection from the fusion engine so the threat
            // posture recalculates immediately.
            if (feedback == "FALSE_POSITIVE" || feedback == "KNOWN_DEVICE") {
                sensorFusionEngine.dismissDetection(alert.threatType.name)
                // Force full recalculation to pick up new trusted networks
                sensorFusionEngine.recalculate()
            }
        }
    }
}
