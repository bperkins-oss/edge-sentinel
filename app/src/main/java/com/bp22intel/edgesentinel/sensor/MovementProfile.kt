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

/**
 * High-level movement state used to drive location-update cadence.
 * Derived from ActivityRecognitionClient transitions when available,
 * otherwise from the accelerometer-based [MotionDetector].
 */
enum class MovementProfile(
    val locationIntervalMs: Long,
    val minDisplacementMeters: Float
) {
    /** Device is still. Infrequent location fixes. */
    STILL(60_000L, 50f),

    /** On foot. Moderate cadence. */
    WALKING(10_000L, 5f),

    /** In a vehicle. Aggressive cadence. */
    IN_VEHICLE(5_000L, 10f),

    /** Classifier has not yet produced a reading — treat as walking to be safe. */
    UNKNOWN(10_000L, 5f);

    companion object {
        fun fromMotionState(state: MotionState): MovementProfile = when (state) {
            MotionState.STATIONARY -> STILL
            MotionState.WALKING -> WALKING
            MotionState.DRIVING -> IN_VEHICLE
            MotionState.UNKNOWN -> UNKNOWN
        }
    }
}
