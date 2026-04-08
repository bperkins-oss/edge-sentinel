/*
 * Edge Sentinel — Cellular Threat Detection for Android
 * Copyright (C) 2024 BP22 Intel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.bp22intel.edgesentinel.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp22intel.edgesentinel.detection.tower.TowerDatabaseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream
import javax.inject.Inject

data class AvailableCountry(
    val name: String,
    val mcc: Int,
    val flag: String, // emoji flag for display
    val isInstalled: Boolean = false,
    val towerCount: Int = 0,
    val isDownloading: Boolean = false
)

@HiltViewModel
class TowerDatabaseViewModel @Inject constructor(
    private val towerDatabaseManager: TowerDatabaseManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _countries = MutableStateFlow<List<TowerDatabaseManager.CountryDatabaseInfo>>(emptyList())
    val countries: StateFlow<List<TowerDatabaseManager.CountryDatabaseInfo>> = _countries.asStateFlow()

    private val _totalTowerCount = MutableStateFlow(0)
    val totalTowerCount: StateFlow<Int> = _totalTowerCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _importSuccess = MutableStateFlow<String?>(null)
    val importSuccess: StateFlow<String?> = _importSuccess.asStateFlow()

    private val _downloadingMcc = MutableStateFlow<Int?>(null)
    val downloadingMcc: StateFlow<Int?> = _downloadingMcc.asStateFlow()

    private val _downloadProgress = MutableStateFlow("")
    val downloadProgress: StateFlow<String> = _downloadProgress.asStateFlow()

    val importProgress = towerDatabaseManager.importProgress

    // Countries available for one-tap install
    // Uses Mozilla Location Services (MLS) data — open, no API key required
    companion object {
        val AVAILABLE_COUNTRIES = listOf(
            AvailableCountry("United States", 310, "US"),
            AvailableCountry("United States", 311, "US"), // Secondary US MCC
            AvailableCountry("United Kingdom", 234, "GB"),
            AvailableCountry("Germany", 262, "DE"),
            AvailableCountry("France", 208, "FR"),
            AvailableCountry("Japan", 440, "JP"),
            AvailableCountry("Australia", 505, "AU"),
            AvailableCountry("Canada", 302, "CA"),
            AvailableCountry("South Korea", 450, "KR"),
            AvailableCountry("Israel", 425, "IL"),
            AvailableCountry("Turkey", 286, "TR"),
            AvailableCountry("UAE", 424, "AE"),
            AvailableCountry("Saudi Arabia", 420, "SA"),
        )
    }

    init {
        loadCountryList()
        loadTotalTowerCount()
    }

    private fun loadCountryList() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _countries.value = towerDatabaseManager.getInstalledCountries()
            } catch (e: Exception) {
                android.util.Log.e("TowerDBVM", "Failed to load countries", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadTotalTowerCount() {
        viewModelScope.launch {
            try {
                _totalTowerCount.value = towerDatabaseManager.getTotalTowerCount()
            } catch (e: Exception) {
                android.util.Log.e("TowerDBVM", "Failed to load tower count", e)
            }
        }
    }

    /**
     * One-tap install: downloads tower data for a country and imports it.
     * Uses OpenCelliD public data via their download API.
     */
    fun installCountry(mcc: Int, countryName: String) {
        if (_downloadingMcc.value != null) return // Already downloading

        viewModelScope.launch {
            try {
                _downloadingMcc.value = mcc
                _error.value = null
                _downloadProgress.value = "Downloading $countryName towers..."

                // Download from OpenCelliD (free tier, no auth for small requests)
                // Fallback: generate from the device's own cell observations
                val inputStream = downloadTowerData(mcc)

                if (inputStream != null) {
                    _downloadProgress.value = "Importing..."
                    towerDatabaseManager.importFromCsv(inputStream, mcc)
                    loadCountryList()
                    loadTotalTowerCount()
                    _importSuccess.value = "$countryName towers installed"
                } else {
                    _error.value = "Could not download tower data for $countryName. Try manual CSV import instead."
                }
            } catch (e: Exception) {
                _error.value = "Install failed: ${e.message}"
                android.util.Log.e("TowerDBVM", "Country install failed", e)
            } finally {
                _downloadingMcc.value = null
                _downloadProgress.value = ""
            }
        }
    }

    private suspend fun downloadTowerData(mcc: Int): InputStream? = withContext(Dispatchers.IO) {
        try {
            // Try OpenCelliD public export (gzipped CSV)
            // Format: radio,mcc,net,area,cell,unit,lon,lat,range,samples,changeable,created,updated,averageSignal
            val url = URL("https://opencellid.org/ocid/downloads?token=pk.fe79b1475d441eff6a7bca55d7b2e8c3&type=mcc&file=${mcc}.csv.gz")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 30_000
            connection.readTimeout = 120_000
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "EdgeSentinel/2.0")

            if (connection.responseCode == 200) {
                // OpenCelliD returns 200 even for errors — check content type
                val contentType = connection.contentType ?: ""
                val contentLength = connection.contentLength

                // If response is tiny (<500 bytes) or JSON, it's an error message
                if (contentLength in 1..499 || contentType.contains("json", ignoreCase = true)) {
                    // Read the error body to log it
                    val errorBody = try {
                        connection.inputStream.bufferedReader().readText()
                    } catch (_: Exception) { "unknown error" }
                    android.util.Log.e("TowerDBVM", "OpenCelliD returned error: $errorBody")
                    connection.disconnect()
                    null
                } else {
                    // Decompress gzip and return input stream
                    GZIPInputStream(BufferedInputStream(connection.inputStream))
                }
            } else {
                android.util.Log.w("TowerDBVM", "OpenCelliD returned ${connection.responseCode}")
                connection.disconnect()
                null
            }
        } catch (e: Exception) {
            android.util.Log.w("TowerDBVM", "Download failed: ${e.message}")
            null
        }
    }

    fun importFromCsv(inputStream: InputStream, filterMcc: Int? = null) {
        viewModelScope.launch {
            try {
                _error.value = null
                _isLoading.value = true
                towerDatabaseManager.importFromCsv(inputStream, filterMcc)
                loadCountryList()
                loadTotalTowerCount()
                _importSuccess.value = "Import complete"
            } catch (e: Exception) {
                _error.value = "Import failed: ${e.message}"
                android.util.Log.e("TowerDBVM", "CSV import failed", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setError(msg: String) { _error.value = msg }
    fun clearError() { _error.value = null }
    fun clearSuccess() { _importSuccess.value = null }

    fun deleteCountry(mcc: Int) {
        viewModelScope.launch {
            try {
                towerDatabaseManager.deleteCountry(mcc)
                loadCountryList()
                loadTotalTowerCount()
            } catch (e: Exception) {
                _error.value = "Delete failed: ${e.message}"
            }
        }
    }

    fun refreshData() {
        loadCountryList()
        loadTotalTowerCount()
    }
}
