/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024-2026 BP22 Intel. All Rights Reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use of this software, in whole or in
 * part, is strictly prohibited without prior written permission from
 * BP22 Intel.
 */

package com.bp22intel.edgesentinel.sensor

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.ActivityTransitionResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Classifies user movement as STILL / WALKING / IN_VEHICLE.
 *
 * Primary source: Google Play Services ActivityRecognitionClient.
 * Fallback: [MotionDetector] (accelerometer variance) when activity
 * detection is unavailable or permission is denied.
 */
@Singleton
class MovementClassifier @Inject constructor(
    @ApplicationContext private val context: Context,
    private val motionDetector: MotionDetector
) {

    companion object {
        private const val ACTION_ACTIVITY_UPDATE =
            "com.bp22intel.edgesentinel.ACTION_ACTIVITY_UPDATE"

        /** Polling interval requested from ActivityRecognitionClient. */
        private const val ACTIVITY_UPDATE_INTERVAL_MS = 5_000L

        private const val ACTION_ACTIVITY_TRANSITION =
            "com.bp22intel.edgesentinel.ACTION_ACTIVITY_TRANSITION"

        /** Confidence threshold below which we ignore a detected activity. */
        private const val MIN_CONFIDENCE = 50
    }

    private val _profile = MutableStateFlow(MovementProfile.UNKNOWN)
    val profile: StateFlow<MovementProfile> = _profile.asStateFlow()

    private val arClient: ActivityRecognitionClient by lazy {
        ActivityRecognition.getClient(context)
    }

    private var pendingIntent: PendingIntent? = null
    private var transitionPendingIntent: PendingIntent? = null
    private var receiver: BroadcastReceiver? = null
    private var transitionReceiver: BroadcastReceiver? = null
    private var fallbackJob: Job? = null
    private var scope: CoroutineScope? = null
    private var usingFallback = false

    /** Start classification. Returns the chosen source for logging. */
    fun start() {
        if (scope != null) return
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        if (hasActivityRecognitionPermission() && tryStartActivityRecognition()) {
            usingFallback = false
            // Also register for instant transition events
            tryStartActivityTransitions()
        } else {
            startAccelerometerFallback()
        }
    }

    fun stop() {
        scope?.cancel()
        scope = null

        if (!usingFallback) {
            pendingIntent?.let { pi ->
                runCatching { arClient.removeActivityUpdates(pi) }
                pi.cancel()
            }
            pendingIntent = null
            transitionPendingIntent?.let { pi ->
                runCatching { arClient.removeActivityTransitionUpdates(pi) }
                pi.cancel()
            }
            transitionPendingIntent = null
            receiver?.let { r ->
                runCatching { context.unregisterReceiver(r) }
            }
            receiver = null
            transitionReceiver?.let { r ->
                runCatching { context.unregisterReceiver(r) }
            }
            transitionReceiver = null
        } else {
            fallbackJob?.cancel()
            fallbackJob = null
            motionDetector.stop()
        }

        _profile.value = MovementProfile.UNKNOWN
    }

    // ── ActivityRecognition path ─────────────────────────────────────────────

    private fun tryStartActivityRecognition(): Boolean {
        return try {
            val intent = Intent(ACTION_ACTIVITY_UPDATE).setPackage(context.packageName)
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            val pi = PendingIntent.getBroadcast(context, 0, intent, flags)
            pendingIntent = pi

            val filter = IntentFilter(ACTION_ACTIVITY_UPDATE)
            val br = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context, received: Intent) {
                    if (received.action != ACTION_ACTIVITY_UPDATE) return
                    if (!ActivityRecognitionResult.hasResult(received)) return
                    val result = ActivityRecognitionResult.extractResult(received) ?: return
                    updateFromActivity(result.mostProbableActivity)
                }
            }
            receiver = br
            ContextCompat.registerReceiver(
                context, br, filter, ContextCompat.RECEIVER_NOT_EXPORTED
            )

            arClient.requestActivityUpdates(ACTIVITY_UPDATE_INTERVAL_MS, pi)
            true
        } catch (_: Throwable) {
            false
        }
    }

    private fun updateFromActivity(activity: DetectedActivity) {
        if (activity.confidence < MIN_CONFIDENCE) return
        _profile.value = when (activity.type) {
            DetectedActivity.STILL -> MovementProfile.STILL
            DetectedActivity.IN_VEHICLE, DetectedActivity.ON_BICYCLE -> MovementProfile.IN_VEHICLE
            DetectedActivity.WALKING, DetectedActivity.RUNNING, DetectedActivity.ON_FOOT -> MovementProfile.WALKING
            else -> _profile.value
        }
    }

    // ── ActivityTransition path (instant transition events) ──────────────────

    private fun tryStartActivityTransitions() {
        try {
            val transitions = listOf(
                ActivityTransition.Builder()
                    .setActivityType(DetectedActivity.STILL)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build(),
                ActivityTransition.Builder()
                    .setActivityType(DetectedActivity.WALKING)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build(),
                ActivityTransition.Builder()
                    .setActivityType(DetectedActivity.RUNNING)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build(),
                ActivityTransition.Builder()
                    .setActivityType(DetectedActivity.ON_FOOT)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build(),
                ActivityTransition.Builder()
                    .setActivityType(DetectedActivity.IN_VEHICLE)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build(),
                ActivityTransition.Builder()
                    .setActivityType(DetectedActivity.ON_BICYCLE)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build()
            )
            val request = ActivityTransitionRequest(transitions)

            val intent = Intent(ACTION_ACTIVITY_TRANSITION).setPackage(context.packageName)
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            val pi = PendingIntent.getBroadcast(context, 1, intent, flags)
            transitionPendingIntent = pi

            val filter = IntentFilter(ACTION_ACTIVITY_TRANSITION)
            val br = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context, received: Intent) {
                    if (received.action != ACTION_ACTIVITY_TRANSITION) return
                    if (!ActivityTransitionResult.hasResult(received)) return
                    val result = ActivityTransitionResult.extractResult(received) ?: return
                    for (event in result.transitionEvents) {
                        if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                            val profile = when (event.activityType) {
                                DetectedActivity.STILL -> MovementProfile.STILL
                                DetectedActivity.IN_VEHICLE, DetectedActivity.ON_BICYCLE -> MovementProfile.IN_VEHICLE
                                DetectedActivity.WALKING, DetectedActivity.RUNNING, DetectedActivity.ON_FOOT -> MovementProfile.WALKING
                                else -> null
                            }
                            if (profile != null) {
                                _profile.value = profile
                            }
                        }
                    }
                }
            }
            transitionReceiver = br
            ContextCompat.registerReceiver(
                context, br, filter, ContextCompat.RECEIVER_NOT_EXPORTED
            )

            @SuppressLint("MissingPermission")
            val task = arClient.requestActivityTransitionUpdates(request, pi)
            // Ignore failures — transition API is supplementary
        } catch (_: Throwable) {
            // Transition API unavailable — polling still works
        }
    }

    // ── Accelerometer fallback ───────────────────────────────────────────────

    private fun startAccelerometerFallback() {
        usingFallback = true
        motionDetector.start()
        val s = scope ?: return
        fallbackJob = s.launch {
            motionDetector.motionState.collectLatest { state ->
                _profile.value = MovementProfile.fromMotionState(state)
            }
        }
    }

    private fun hasActivityRecognitionPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
