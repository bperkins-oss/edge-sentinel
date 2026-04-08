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

package com.bp22intel.edgesentinel.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp22intel.edgesentinel.detection.geo.GeolocatedThreat
import com.bp22intel.edgesentinel.detection.geo.ThreatGeolocation
import com.bp22intel.edgesentinel.domain.repository.AlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThreatMapViewModel @Inject constructor(
    private val alertRepository: AlertRepository,
    private val threatGeolocation: ThreatGeolocation,
    @ApplicationContext private val context: Context
) : ViewModel(), LocationListener {

    companion object {
        private const val LOCATION_UPDATE_MIN_TIME = 10000L // 10 seconds
        private const val LOCATION_UPDATE_MIN_DISTANCE = 10f // 10 meters
        private const val MAX_ALERTS_FOR_MAP = 50 // Limit to prevent performance issues
    }

    private val _geolocatedThreats = MutableStateFlow<List<GeolocatedThreat>>(emptyList())
    val geolocatedThreats: StateFlow<List<GeolocatedThreat>> = _geolocatedThreats.asStateFlow()

    private val _userLocation = MutableStateFlow(Pair(0.0, 0.0))
    val userLocation: StateFlow<Pair<Double, Double>> = _userLocation.asStateFlow()

    private val _isLocationEnabled = MutableStateFlow(false)
    val isLocationEnabled: StateFlow<Boolean> = _isLocationEnabled.asStateFlow()

    private val locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    init {
        startLocationUpdates()
        observeAlertsAndGeolocate()
    }

    private fun startLocationUpdates() {
        if (!hasLocationPermission()) {
            _isLocationEnabled.value = false
            return
        }

        try {
            val provider = when {
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                    LocationManager.GPS_PROVIDER
                }
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> {
                    LocationManager.NETWORK_PROVIDER
                }
                else -> {
                    _isLocationEnabled.value = false
                    return
                }
            }

            locationManager.requestLocationUpdates(
                provider,
                LOCATION_UPDATE_MIN_TIME,
                LOCATION_UPDATE_MIN_DISTANCE,
                this
            )

            // Get last known location immediately
            locationManager.getLastKnownLocation(provider)?.let { location ->
                onLocationChanged(location)
            }

            _isLocationEnabled.value = true
        } catch (e: SecurityException) {
            _isLocationEnabled.value = false
        }
    }

    private fun observeAlertsAndGeolocate() {
        viewModelScope.launch {
            combine(
                alertRepository.getRecentAlerts(MAX_ALERTS_FOR_MAP),
                _userLocation
            ) { alerts, location ->
                Pair(alerts, location)
            }.collectLatest { (alerts, location) ->
                try {
                    val geolocated = threatGeolocation.geolocateThreats(
                        alerts = alerts,
                        userLat = location.first,
                        userLng = location.second
                    )
                    _geolocatedThreats.value = geolocated
                } catch (e: Exception) {
                    // Log error and continue with empty list
                    _geolocatedThreats.value = emptyList()
                }
            }
        }
    }

    fun updateUserLocation(latitude: Double, longitude: Double) {
        _userLocation.value = Pair(latitude, longitude)
    }

    fun refreshThreats() {
        // Trigger a refresh by re-emitting current location
        val current = _userLocation.value
        _userLocation.value = current
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // LocationListener implementation
    override fun onLocationChanged(location: Location) {
        _userLocation.value = Pair(location.latitude, location.longitude)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Handle provider status changes if needed
    }

    override fun onProviderEnabled(provider: String) {
        if (!_isLocationEnabled.value) {
            startLocationUpdates()
        }
    }

    override fun onProviderDisabled(provider: String) {
        // Check if any provider is still available
        val hasEnabledProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        
        if (!hasEnabledProvider) {
            _isLocationEnabled.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            locationManager.removeUpdates(this)
        } catch (e: SecurityException) {
            // Ignore - already handled
        }
    }
}