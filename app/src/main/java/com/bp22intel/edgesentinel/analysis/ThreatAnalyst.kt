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

package com.bp22intel.edgesentinel.analysis

import com.bp22intel.edgesentinel.domain.model.Alert
import com.bp22intel.edgesentinel.domain.model.CellTower
import com.bp22intel.edgesentinel.domain.model.Confidence
import com.bp22intel.edgesentinel.domain.model.NetworkType
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import com.bp22intel.edgesentinel.domain.model.ThreatType
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI Threat Analyst — rule-based, fully offline engine that interprets raw
 * detection data and produces plain-English situation assessments.
 *
 * Think of this as the intelligence analyst sitting between the raw sensor
 * feeds (detectors) and the executive dashboard (UI). Detectors say *what*
 * happened; the analyst explains *what it means* and *what to do about it*.
 *
 * All analysis is deterministic and runs on-device — no network calls.
 */
@Singleton
class ThreatAnalyst @Inject constructor() {

    // ──────────────────────────────────────────────────────────────────────
    //  Public API
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Analyse a single alert and produce a human-readable assessment.
     *
     * The returned [AlertAnalysis] contains everything the UI needs to
     * display a clear, actionable explanation to the user.
     */
    fun analyzeAlert(alert: Alert): AlertAnalysis {
        val details = alert.detailsJson.parseJsonSafe()
        return when (alert.threatType) {
            ThreatType.FAKE_BTS        -> analyzeFakeBts(alert, details)
            ThreatType.NETWORK_DOWNGRADE -> analyzeNetworkDowngrade(alert, details)
            ThreatType.SILENT_SMS      -> analyzeSilentSms(alert, details)
            ThreatType.TRACKING_PATTERN -> analyzeTrackingPattern(alert, details)
            ThreatType.CIPHER_ANOMALY  -> analyzeCipherAnomaly(alert, details)
            ThreatType.SIGNAL_ANOMALY  -> analyzeSignalAnomaly(alert, details)
            ThreatType.NR_ANOMALY      -> analyzeNrAnomaly(alert, details)
        }
    }

    /**
     * Synthesise multiple alerts and environmental context into a holistic
     * situation brief — the "big picture" for the user.
     *
     * @param alerts   Current active/recent alerts (may be empty).
     * @param cellInfo Current serving cell information, if available.
     * @param isMoving Whether the device is in motion (walking/driving).
     */
    fun analyzeSituation(
        alerts: List<Alert>,
        cellInfo: CellTower?,
        isMoving: Boolean
    ): SituationBrief {
        if (alerts.isEmpty()) {
            return SituationBrief(
                summary = "No active alerts. Your cellular environment looks normal.",
                overallRisk = RiskLevel.LOW,
                topConcerns = emptyList(),
                recommendations = listOf("No action needed — carry on as normal."),
                allClear = true
            )
        }

        // Analyse each alert individually, enriched with motion context.
        val analyses = alerts.map { analyzeAlert(it) }
        val highestRisk = analyses.maxOf { it.riskLevel }

        // Build top concerns: unique plain-English descriptions, worst first.
        val topConcerns = analyses
            .sortedByDescending { it.riskLevel }
            .map { it.plainEnglish }
            .distinct()
            .take(MAX_TOP_CONCERNS)

        // Deduplicate and prioritise recommendations.
        val recommendations = buildRecommendations(analyses, highestRisk, isMoving)

        val allClear = highestRisk == RiskLevel.LOW &&
            analyses.none { it.shouldWorry }

        val summary = buildSituationSummary(alerts, analyses, highestRisk, cellInfo, isMoving)

        return SituationBrief(
            summary = summary,
            overallRisk = highestRisk,
            topConcerns = topConcerns,
            recommendations = recommendations,
            allClear = allClear
        )
    }

    // ──────────────────────────────────────────────────────────────────────
    //  Per-threat-type analysis logic
    // ──────────────────────────────────────────────────────────────────────

    /**
     * FAKE_BTS — potential rogue base station / IMSI catcher.
     *
     * Key signals: unusually high signal strength while stationary suggests
     * a nearby portable transmitter. Moving through a strong-signal area
     * is less suspicious (could be a legitimate macro cell).
     */
    private fun analyzeFakeBts(alert: Alert, details: JSONObject): AlertAnalysis {
        val signalStrength = details.optInt("signalStrength", Int.MIN_VALUE)
        val isMoving = details.optBoolean("isMoving", false)
        val nearbyTowerCount = details.optInt("nearbyTowerCount", -1)
        val isHighSignal = signalStrength != Int.MIN_VALUE && signalStrength > -60

        // Stationary + high signal + few legitimate towers = very suspicious.
        val riskLevel = when {
            !isMoving && isHighSignal && nearbyTowerCount in 0..2 -> RiskLevel.CRITICAL
            !isMoving && isHighSignal -> RiskLevel.HIGH
            !isMoving -> RiskLevel.MEDIUM
            isHighSignal -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        val shouldWorry = riskLevel >= RiskLevel.HIGH

        val plainEnglish = when (riskLevel) {
            RiskLevel.CRITICAL ->
                "A suspicious cell tower with an unusually strong signal appeared nearby while " +
                "you are stationary. This is consistent with a portable surveillance device " +
                "(IMSI catcher) operating close to your location."
            RiskLevel.HIGH ->
                "A cell tower with characteristics of a fake base station is broadcasting " +
                "a very strong signal near you. This could be a surveillance device."
            RiskLevel.MEDIUM ->
                "Some cell tower characteristics look unusual. This may indicate a fake " +
                "base station, but could also be a legitimate tower configuration."
            else ->
                "A cell tower showed some irregular characteristics but the pattern is " +
                "more consistent with normal network behaviour while in transit."
        }

        val recommendation = when (riskLevel) {
            RiskLevel.CRITICAL ->
                "Avoid sensitive calls and messaging. Move to a different location " +
                "at least several blocks away and check if the alert clears."
            RiskLevel.HIGH ->
                "Avoid sensitive communications until you leave this area. Use " +
                "encrypted messaging apps for anything confidential."
            RiskLevel.MEDIUM ->
                "Use encrypted messaging for sensitive conversations as a precaution. " +
                "Monitor whether additional alerts appear."
            else ->
                "This is likely normal. No action needed, but keep monitoring."
        }

        val possibleCauses = buildList {
            if (riskLevel >= RiskLevel.HIGH) add("IMSI catcher or rogue base station nearby")
            add("Legitimate new cell tower with unusual configuration")
            add("Temporary carrier equipment (event coverage, maintenance)")
            if (isMoving) add("Passing through an area with strong tower coverage")
        }

        return AlertAnalysis(
            plainEnglish = plainEnglish,
            riskLevel = riskLevel,
            recommendation = recommendation,
            confidence = confidenceToFloat(alert.confidence, riskLevel),
            possibleCauses = possibleCauses,
            shouldWorry = shouldWorry
        )
    }

    /**
     * NETWORK_DOWNGRADE — forced fallback from 4G/5G to an older protocol.
     *
     * In urban areas this shouldn't happen (dense 4G/5G coverage), so it's
     * more concerning. In rural areas it may be normal coverage gaps.
     */
    private fun analyzeNetworkDowngrade(alert: Alert, details: JSONObject): AlertAnalysis {
        val fromNetwork = details.optString("fromNetwork", "")
        val toNetwork = details.optString("toNetwork", "")
        val isUrban = details.optBoolean("isUrban", true) // Default to urban (more cautious)

        val isSevereDowngrade = toNetwork in listOf("GSM", "2G", "CDMA")

        val riskLevel = when {
            isSevereDowngrade && isUrban -> RiskLevel.HIGH
            isSevereDowngrade -> RiskLevel.MEDIUM
            isUrban -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        val shouldWorry = riskLevel >= RiskLevel.HIGH

        val fromLabel = fromNetwork.ifEmpty { "a newer network" }
        val toLabel = toNetwork.ifEmpty { "an older network" }

        val plainEnglish = when (riskLevel) {
            RiskLevel.HIGH ->
                "Your phone was forced from $fromLabel down to $toLabel in an area " +
                "with good coverage. This is a common tactic used by surveillance " +
                "equipment to disable modern encryption."
            RiskLevel.MEDIUM ->
                "Your phone dropped from $fromLabel to $toLabel. In your current area " +
                "this is somewhat unusual and could indicate an attempt to weaken " +
                "your connection's security."
            else ->
                "Your phone switched from $fromLabel to $toLabel. In rural or fringe " +
                "coverage areas, this is often normal behaviour."
        }

        val recommendation = when (riskLevel) {
            RiskLevel.HIGH ->
                "Avoid phone calls and standard SMS — they may not be encrypted. " +
                "Use end-to-end encrypted apps (Signal, WhatsApp) for all communication."
            RiskLevel.MEDIUM ->
                "Be cautious with sensitive calls. Prefer encrypted messaging apps " +
                "until your connection returns to a modern network."
            else ->
                "This is likely a normal coverage gap. No special action needed."
        }

        val possibleCauses = buildList {
            if (isUrban && isSevereDowngrade) add("Deliberate downgrade attack to strip encryption")
            add("Network congestion causing fallback")
            add("Coverage gap or building penetration issues")
            add("Carrier network maintenance or outage")
        }

        return AlertAnalysis(
            plainEnglish = plainEnglish,
            riskLevel = riskLevel,
            recommendation = recommendation,
            confidence = confidenceToFloat(alert.confidence, riskLevel),
            possibleCauses = possibleCauses,
            shouldWorry = shouldWorry
        )
    }

    /**
     * SILENT_SMS — invisible text message used for location pinging.
     *
     * Always concerning. These are a well-documented surveillance technique:
     * the target's phone processes them but shows no notification.
     */
    private fun analyzeSilentSms(alert: Alert, details: JSONObject): AlertAnalysis {
        val count = details.optInt("count", 1)

        val riskLevel = when {
            count >= 5 -> RiskLevel.CRITICAL
            count >= 2 -> RiskLevel.HIGH
            else -> RiskLevel.MEDIUM
        }

        val plainEnglish = when {
            count >= 5 ->
                "Multiple invisible messages ($count) have been sent to your phone in a " +
                "short period. This is a strong indicator of active location tracking — " +
                "someone is pinging your device to determine where you are."
            count >= 2 ->
                "Several silent messages ($count) were detected. These invisible texts are " +
                "used to track your phone's location without your knowledge."
            else ->
                "An invisible text message was sent to your phone. Silent SMS is a known " +
                "technique used by law enforcement and others to ping your location."
        }

        val recommendation = when (riskLevel) {
            RiskLevel.CRITICAL ->
                "Your location is actively being tracked. If this is unexpected, " +
                "enable airplane mode immediately, then assess your situation. " +
                "Consider powering off the device."
            RiskLevel.HIGH ->
                "Someone may be tracking your location. Consider enabling airplane " +
                "mode if you need to stop being located. Avoid patterns in your movement."
            else ->
                "Be aware that your location may have been pinged. A single occurrence " +
                "could be anomalous, but stay alert for additional silent messages."
        }

        val possibleCauses = buildList {
            add("Active location tracking via silent SMS ping")
            add("Law enforcement or intelligence location request")
            if (count == 1) add("Network diagnostic message (less likely)")
        }

        return AlertAnalysis(
            plainEnglish = plainEnglish,
            riskLevel = riskLevel,
            recommendation = recommendation,
            confidence = confidenceToFloat(alert.confidence, riskLevel),
            possibleCauses = possibleCauses,
            shouldWorry = true // Silent SMS is always concerning.
        )
    }

    /**
     * TRACKING_PATTERN — Location Area Code oscillation suggesting tracking.
     *
     * While stationary, rapid LAC changes mean the network (or something
     * impersonating it) is repeatedly re-registering the device — a classic
     * triangulation technique. While driving, LAC changes are expected.
     */
    private fun analyzeTrackingPattern(alert: Alert, details: JSONObject): AlertAnalysis {
        val isMoving = details.optBoolean("isMoving", false)
        val lacChanges = details.optInt("lacChanges", 0)

        val riskLevel = when {
            !isMoving && lacChanges >= 4 -> RiskLevel.CRITICAL
            !isMoving && lacChanges >= 2 -> RiskLevel.HIGH
            !isMoving -> RiskLevel.MEDIUM
            lacChanges >= 6 -> RiskLevel.MEDIUM  // Excessive even for driving
            else -> RiskLevel.LOW
        }

        val shouldWorry = riskLevel >= RiskLevel.HIGH

        val plainEnglish = when {
            !isMoving && riskLevel >= RiskLevel.HIGH ->
                "Your phone is repeatedly re-registering with different network zones " +
                "even though you haven't moved. This pattern is used to triangulate " +
                "your exact location — someone may be actively tracking you."
            !isMoving ->
                "Some unusual network zone switching was detected while you're stationary. " +
                "This could indicate location tracking, but the pattern isn't conclusive yet."
            riskLevel >= RiskLevel.MEDIUM ->
                "An unusually high number of network zone changes occurred while moving. " +
                "While some handovers are normal during travel, this rate is elevated."
            else ->
                "Network zone changes detected during movement. This is typical " +
                "behaviour as your phone hands off between cell towers while in transit."
        }

        val recommendation = when (riskLevel) {
            RiskLevel.CRITICAL ->
                "Active tracking is likely. Enable airplane mode if you need to break " +
                "the tracking. Avoid this location for sensitive activities."
            RiskLevel.HIGH ->
                "Possible active tracking. Consider enabling airplane mode temporarily " +
                "and moving to a different area to see if the pattern stops."
            RiskLevel.MEDIUM ->
                "Monitor the situation. If you see this alert repeatedly while " +
                "stationary, take it more seriously."
            else ->
                "This is likely normal handover behaviour during travel. No action needed."
        }

        val possibleCauses = buildList {
            if (!isMoving) {
                add("Active location triangulation via forced re-registration")
                add("Nearby IMSI catcher causing network oscillation")
            }
            add("Normal cell tower handovers during movement")
            add("Network load balancing between sectors")
            add("Carrier network reconfiguration")
        }

        return AlertAnalysis(
            plainEnglish = plainEnglish,
            riskLevel = riskLevel,
            recommendation = recommendation,
            confidence = confidenceToFloat(alert.confidence, riskLevel),
            possibleCauses = possibleCauses,
            shouldWorry = shouldWorry
        )
    }

    /**
     * CIPHER_ANOMALY — encryption weakened or removed on the air interface.
     *
     * Null cipher (A5/0 or equivalent) = critical, the call is in the clear.
     * Downgrade to A5/1 (broken) = high. Anything else = medium.
     */
    private fun analyzeCipherAnomaly(alert: Alert, details: JSONObject): AlertAnalysis {
        val cipher = details.optString("cipher", "").uppercase()
        val previousCipher = details.optString("previousCipher", "").uppercase()

        val isNullCipher = cipher in listOf("A5/0", "A50", "NONE", "NULL", "")
        val isWeakCipher = cipher in listOf("A5/1", "A51")

        val riskLevel = when {
            isNullCipher -> RiskLevel.CRITICAL
            isWeakCipher -> RiskLevel.HIGH
            else -> RiskLevel.MEDIUM
        }

        val shouldWorry = riskLevel >= RiskLevel.HIGH

        val plainEnglish = when {
            isNullCipher ->
                "Your phone's connection has NO encryption. Every call, text, and data " +
                "packet is being transmitted in the open. Anyone with a radio receiver " +
                "can listen to your communications right now."
            isWeakCipher ->
                "Your phone's encryption was downgraded to a weak, broken algorithm " +
                "(${cipher}). A sophisticated attacker can decrypt your calls and " +
                "messages in real time."
            else ->
                "An unusual change in your phone's encryption settings was detected. " +
                "The current cipher ($cipher) may be weaker than expected" +
                if (previousCipher.isNotEmpty()) " (was $previousCipher)." else "."
        }

        val recommendation = when {
            isNullCipher ->
                "STOP all voice calls and standard SMS immediately. Your communications " +
                "are completely unprotected. Use only end-to-end encrypted apps (Signal, " +
                "WhatsApp) or wait until encryption is restored."
            isWeakCipher ->
                "Avoid voice calls and standard SMS for anything sensitive. Use " +
                "end-to-end encrypted messaging apps. This cipher can be broken " +
                "with commodity hardware."
            else ->
                "Prefer encrypted messaging apps for sensitive conversations. " +
                "Monitor for further encryption changes."
        }

        val possibleCauses = buildList {
            if (isNullCipher) {
                add("Active interception — someone is stripping encryption to eavesdrop")
                add("Misconfigured cell tower (rare but possible)")
            }
            if (isWeakCipher) {
                add("Deliberate cipher downgrade for surveillance")
                add("Legacy network infrastructure forcing weaker encryption")
            }
            add("Network configuration change during maintenance")
            add("Handover to older equipment with limited cipher support")
        }

        return AlertAnalysis(
            plainEnglish = plainEnglish,
            riskLevel = riskLevel,
            recommendation = recommendation,
            confidence = confidenceToFloat(alert.confidence, riskLevel),
            possibleCauses = possibleCauses,
            shouldWorry = shouldWorry
        )
    }

    /**
     * SIGNAL_ANOMALY — unexpected signal strength patterns.
     *
     * A brand-new tower appearing with very high signal is suspicious (portable
     * transmitter). General fluctuations are usually just the RF environment.
     */
    private fun analyzeSignalAnomaly(alert: Alert, details: JSONObject): AlertAnalysis {
        val isNewTower = details.optBoolean("isNewTower", false)
        val signalStrength = details.optInt("signalStrength", Int.MIN_VALUE)
        val isHighSignal = signalStrength != Int.MIN_VALUE && signalStrength > -60
        val signalDelta = details.optInt("signalDelta", 0)
        val isLargeFluctuation = kotlin.math.abs(signalDelta) > 20

        val riskLevel = when {
            isNewTower && isHighSignal -> RiskLevel.HIGH
            isNewTower -> RiskLevel.MEDIUM
            isLargeFluctuation && isHighSignal -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        val shouldWorry = riskLevel >= RiskLevel.HIGH

        val plainEnglish = when {
            isNewTower && isHighSignal ->
                "A previously unseen cell tower just appeared with an unusually strong " +
                "signal. New towers don't normally appear overnight with high power — " +
                "this could be a portable surveillance device."
            isNewTower ->
                "A new cell tower was detected in your area that wasn't seen before. " +
                "This is worth monitoring but may be legitimate new infrastructure."
            isLargeFluctuation ->
                "Unusual signal strength changes were detected. Large swings can " +
                "indicate interference or environmental changes, but are usually harmless."
            else ->
                "Minor signal variations were detected. This is typically caused by " +
                "normal environmental factors and is not a concern."
        }

        val recommendation = when (riskLevel) {
            RiskLevel.HIGH ->
                "A suspicious new tower is nearby. Use encrypted communications and " +
                "consider moving to a different area to see if the tower follows you."
            RiskLevel.MEDIUM ->
                "Keep an eye on this. If the new tower persists or you see other " +
                "alerts (fake BTS, cipher changes), treat it more seriously."
            else ->
                "This is likely normal. No action needed."
        }

        val possibleCauses = buildList {
            if (isNewTower && isHighSignal) add("Portable surveillance device (IMSI catcher)")
            if (isNewTower) add("New legitimate cell tower installation")
            add("Environmental interference (buildings, weather)")
            add("Network load balancing adjustments")
            if (isLargeFluctuation) add("Physical obstruction or movement near tower")
        }

        return AlertAnalysis(
            plainEnglish = plainEnglish,
            riskLevel = riskLevel,
            recommendation = recommendation,
            confidence = confidenceToFloat(alert.confidence, riskLevel),
            possibleCauses = possibleCauses,
            shouldWorry = shouldWorry
        )
    }

    /**
     * NR_ANOMALY — 5G-specific anomalies, typically unexpected downgrades.
     *
     * Losing 5G in an urban area with known coverage is suspicious.
     * In rural/fringe areas, 5G drop to 4G is common and expected.
     */
    private fun analyzeNrAnomaly(alert: Alert, details: JSONObject): AlertAnalysis {
        val isUrban = details.optBoolean("isUrban", true)
        val fromNr = details.optBoolean("from5G", true)
        val toNetwork = details.optString("toNetwork", "LTE")
        val isDowngradeToLegacy = toNetwork.uppercase() in listOf("GSM", "2G", "CDMA", "WCDMA", "3G")

        val riskLevel = when {
            isUrban && isDowngradeToLegacy -> RiskLevel.HIGH
            isUrban && fromNr -> RiskLevel.MEDIUM
            isDowngradeToLegacy -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        val shouldWorry = riskLevel >= RiskLevel.HIGH

        val plainEnglish = when {
            isUrban && isDowngradeToLegacy ->
                "Your 5G connection was dropped all the way to $toNetwork in an area " +
                "with good 5G coverage. Skipping past 4G to a legacy network is highly " +
                "unusual and may indicate forced downgrade for surveillance."
            isUrban && fromNr ->
                "Your 5G connection dropped to $toNetwork in an area with known 5G " +
                "coverage. While brief drops can happen, this is worth watching."
            isDowngradeToLegacy ->
                "Your connection fell back from 5G to $toNetwork. In areas with " +
                "limited 5G this can happen, but the jump to a legacy network is notable."
            else ->
                "Your 5G connection briefly dropped to $toNetwork. In your current area " +
                "this is likely a normal coverage transition."
        }

        val recommendation = when (riskLevel) {
            RiskLevel.HIGH ->
                "Avoid sensitive calls — your connection's security has been significantly " +
                "weakened. Use encrypted messaging apps and monitor for additional alerts."
            RiskLevel.MEDIUM ->
                "Monitor the situation. If your connection doesn't return to 5G soon " +
                "or you see other security alerts, take precautions."
            else ->
                "This is likely a normal 5G coverage gap. No action needed."
        }

        val possibleCauses = buildList {
            if (isUrban && isDowngradeToLegacy) {
                add("Forced network downgrade for surveillance purposes")
            }
            add("5G signal obstruction or congestion")
            add("Carrier network maintenance or reconfiguration")
            add("Coverage gap at cell edge")
            if (!isUrban) add("Limited 5G deployment in this area")
        }

        return AlertAnalysis(
            plainEnglish = plainEnglish,
            riskLevel = riskLevel,
            recommendation = recommendation,
            confidence = confidenceToFloat(alert.confidence, riskLevel),
            possibleCauses = possibleCauses,
            shouldWorry = shouldWorry
        )
    }

    // ──────────────────────────────────────────────────────────────────────
    //  Situation brief helpers
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Build a concise executive summary from all current alerts and context.
     */
    private fun buildSituationSummary(
        alerts: List<Alert>,
        analyses: List<AlertAnalysis>,
        highestRisk: RiskLevel,
        cellInfo: CellTower?,
        isMoving: Boolean
    ): String {
        val alertCount = alerts.size
        val worrySomeCount = analyses.count { it.shouldWorry }
        val threatTypes = alerts.map { it.threatType }.distinct()

        val motionContext = if (isMoving) "while in transit" else "at your current location"

        return when {
            highestRisk == RiskLevel.CRITICAL -> buildString {
                append("CRITICAL SITUATION: ")
                append("$alertCount active alert${alertCount.plural()} detected $motionContext. ")
                append("$worrySomeCount require${if (worrySomeCount == 1) "s" else ""} immediate attention. ")
                if (threatTypes.size > 1) {
                    append("Multiple attack indicators present — this may be a coordinated effort. ")
                }
                append("Take protective action now.")
            }
            highestRisk == RiskLevel.HIGH -> buildString {
                append("HIGH CONCERN: ")
                append("$alertCount alert${alertCount.plural()} detected $motionContext")
                if (worrySomeCount > 0) {
                    append(", $worrySomeCount warranting concern")
                }
                append(". ")
                append("Precautionary measures recommended.")
            }
            highestRisk == RiskLevel.MEDIUM -> buildString {
                append("$alertCount alert${alertCount.plural()} detected $motionContext. ")
                append("Nothing requires immediate action, but the situation warrants monitoring. ")
                if (cellInfo != null) {
                    append("Connected to ${cellInfo.networkType.generation} (cell ${cellInfo.cid}).")
                }
            }
            else -> buildString {
                append("$alertCount minor alert${alertCount.plural()} detected $motionContext. ")
                append("All appear consistent with normal network behaviour.")
            }
        }
    }

    /**
     * Produce a deduplicated, prioritised list of recommendations.
     */
    private fun buildRecommendations(
        analyses: List<AlertAnalysis>,
        highestRisk: RiskLevel,
        isMoving: Boolean
    ): List<String> {
        val recs = mutableListOf<String>()

        // Add individual recommendations from worst to least.
        analyses
            .sortedByDescending { it.riskLevel }
            .forEach { analysis ->
                if (analysis.recommendation !in recs) {
                    recs.add(analysis.recommendation)
                }
            }

        // Universal recommendations based on overall risk.
        if (highestRisk >= RiskLevel.HIGH) {
            val encrypted = "Use end-to-end encrypted apps (Signal, WhatsApp) for all communications."
            if (encrypted !in recs) recs.add(0, encrypted)
        }
        if (highestRisk >= RiskLevel.CRITICAL && !isMoving) {
            val relocate = "Consider relocating — move at least several blocks and check if alerts clear."
            if (relocate !in recs) recs.add(1, relocate)
        }

        return recs.take(MAX_RECOMMENDATIONS)
    }

    // ──────────────────────────────────────────────────────────────────────
    //  Utilities
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Convert the raw detector [Confidence] and contextual [RiskLevel] into
     * a single 0-1 float for the analyst assessment.
     */
    private fun confidenceToFloat(confidence: Confidence, risk: RiskLevel): Float {
        val base = when (confidence) {
            Confidence.LOW -> 0.3f
            Confidence.MEDIUM -> 0.6f
            Confidence.HIGH -> 0.85f
        }
        // Boost slightly when risk context aligns with high confidence.
        return when {
            risk >= RiskLevel.HIGH && confidence == Confidence.HIGH -> (base + 0.1f).coerceAtMost(1.0f)
            risk <= RiskLevel.LOW && confidence == Confidence.LOW -> (base - 0.1f).coerceAtLeast(0.0f)
            else -> base
        }
    }

    /** Pluralise helper — returns "s" for counts != 1. */
    private fun Int.plural(): String = if (this == 1) "" else "s"

    /** Safely parse a JSON string, returning an empty object on failure. */
    private fun String.parseJsonSafe(): JSONObject {
        return try {
            JSONObject(this)
        } catch (_: Exception) {
            JSONObject()
        }
    }

    companion object {
        /** Maximum number of top concerns to include in a situation brief. */
        private const val MAX_TOP_CONCERNS = 5

        /** Maximum number of recommendations in a situation brief. */
        private const val MAX_RECOMMENDATIONS = 6
    }
}
