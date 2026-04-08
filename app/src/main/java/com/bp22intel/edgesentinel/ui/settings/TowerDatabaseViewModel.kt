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

package com.bp22intel.edgesentinel.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bp22intel.edgesentinel.detection.tower.TowerDatabaseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class TowerDatabaseViewModel @Inject constructor(
    private val towerDatabaseManager: TowerDatabaseManager
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

    val importProgress = towerDatabaseManager.importProgress

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
                // Handle error - maybe log or show toast
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
                // Handle error
            }
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

    fun setError(msg: String) {
        _error.value = msg
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _importSuccess.value = null
    }

    fun deleteCountry(mcc: Int) {
        viewModelScope.launch {
            try {
                towerDatabaseManager.deleteCountry(mcc)
                loadCountryList() // Refresh the list
                loadTotalTowerCount() // Refresh total count
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun refreshData() {
        loadCountryList()
        loadTotalTowerCount()
    }
}