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
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Movement-aware GPS tracker.
 *
 * Wraps [FusedLocationProviderClient] and re-subscribes whenever the
 * requested [MovementProfile] changes so that cadence adapts to whether
 * the user is still, walking, or in a vehicle.
 */
@Singleton
class LocationTracker @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val client: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _lastLocation = MutableStateFlow<Location?>(null)
    val lastLocation: StateFlow<Location?> = _lastLocation.asStateFlow()

    private val _currentProfile = MutableStateFlow(MovementProfile.UNKNOWN)
    val currentProfile: StateFlow<MovementProfile> = _currentProfile.asStateFlow()

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { _lastLocation.value = it }
        }
    }

    private var subscribed = false

    /** Snapshot of the latest known location, or null if none yet. */
    fun snapshot(): Location? = _lastLocation.value

    /**
     * Start receiving location updates at a cadence matching [profile].
     * Calling again with a different profile re-subscribes with new parameters.
     * No-op if fine-location permission is not granted.
     */
    fun start(profile: MovementProfile) {
        if (!hasFineLocation()) return
        if (subscribed && _currentProfile.value == profile) return

        // Re-subscribe with new request
        if (subscribed) client.removeLocationUpdates(callback)

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            profile.locationIntervalMs
        )
            .setMinUpdateIntervalMillis(profile.locationIntervalMs / 2)
            .setMaxWaitTime(profile.locationIntervalMs * 2)
            .setMinUpdateDistanceMeters(profile.minDisplacementMeters)
            .setWaitForAccurateLocation(false)
            .build()

        @SuppressLint("MissingPermission")
        val task = client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        task.addOnSuccessListener { subscribed = true }
        _currentProfile.value = profile

        // Seed with last cached fix so downstream consumers don't wait for first callback.
        @SuppressLint("MissingPermission")
        client.lastLocation.addOnSuccessListener { loc ->
            if (loc != null && _lastLocation.value == null) {
                _lastLocation.value = loc
            }
        }
    }

    fun stop() {
        if (!subscribed) return
        client.removeLocationUpdates(callback)
        subscribed = false
        _currentProfile.value = MovementProfile.UNKNOWN
    }

    private fun hasFineLocation(): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
}
