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

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bp22intel.edgesentinel.ui.components.SectionHeader
import com.bp22intel.edgesentinel.ui.theme.AccentBlue
import com.bp22intel.edgesentinel.ui.theme.BackgroundPrimary
import com.bp22intel.edgesentinel.ui.theme.StatusClear
import com.bp22intel.edgesentinel.ui.theme.Surface
import com.bp22intel.edgesentinel.ui.theme.TextPrimary
import com.bp22intel.edgesentinel.ui.theme.TextSecondary

@Composable
fun TowerDatabaseScreen(
    viewModel: TowerDatabaseViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val countries by viewModel.countries.collectAsState()
    val totalTowerCount by viewModel.totalTowerCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val importSuccess by viewModel.importSuccess.collectAsState()
    val downloadingMcc by viewModel.downloadingMcc.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()

    // Fallback: manual CSV import via file picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                context.contentResolver.openInputStream(selectedUri)?.let { inputStream ->
                    viewModel.importFromCsv(inputStream)
                } ?: viewModel.setError("Could not open file")
            } catch (e: Exception) {
                viewModel.setError("Import failed: ${e.message}")
            }
        }
    }

    // Figure out which MCCs are already installed
    val installedMccs = countries.map { it.mcc }.toSet()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$totalTowerCount",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (totalTowerCount > 0) StatusClear else TextSecondary
                    )
                    Text(
                        text = "Known Towers",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    if (totalTowerCount > 0) {
                        Text(
                            text = "${countries.size} countries installed",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Error/Success messages
        error?.let { msg ->
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.15f))) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(msg, style = MaterialTheme.typography.bodySmall, color = Color(0xFFEF4444), modifier = Modifier.weight(1f))
                        TextButton(onClick = { viewModel.clearError() }) { Text("OK", color = Color(0xFFEF4444)) }
                    }
                }
            }
        }
        importSuccess?.let { msg ->
            item {
                Card(colors = CardDefaults.cardColors(containerColor = StatusClear.copy(alpha = 0.15f))) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(msg, style = MaterialTheme.typography.bodySmall, color = StatusClear, modifier = Modifier.weight(1f))
                        TextButton(onClick = { viewModel.clearSuccess() }) { Text("OK", color = StatusClear) }
                    }
                }
            }
        }

        // Download progress
        if (downloadingMcc != null) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = AccentBlue.copy(alpha = 0.1f))) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = AccentBlue, strokeWidth = 2.dp)
                        Text(downloadProgress, style = MaterialTheme.typography.bodySmall, color = AccentBlue)
                    }
                }
            }
        }

        // One-tap install section
        item {
            SectionHeader(title = "Install Tower Database")
        }

        item {
            Text(
                "Tap a country to download and install its tower database. This verifies your connected towers against known legitimate ones.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // Country install buttons — grouped by region
        val countryGroups = TowerDatabaseViewModel.AVAILABLE_COUNTRIES
            .distinctBy { it.mcc }
            .map { country ->
                country.copy(
                    isInstalled = installedMccs.contains(country.mcc),
                    towerCount = countries.find { it.mcc == country.mcc }?.towerCount ?: 0
                )
            }

        items(countryGroups, key = { it.mcc }) { country ->
            val isDownloading = downloadingMcc == country.mcc
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = country.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (country.isInstalled) "${country.towerCount} towers" else "MCC ${country.mcc}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (country.isInstalled) StatusClear else TextSecondary
                        )
                    }

                    if (country.isInstalled) {
                        // Installed — show checkmark + delete
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Installed",
                            tint = StatusClear,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { viewModel.deleteCountry(country.mcc) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Remove",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else if (isDownloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = AccentBlue,
                            strokeWidth = 2.dp
                        )
                    } else {
                        // Not installed — install button
                        Button(
                            onClick = { viewModel.installCountry(country.mcc, country.name) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentBlue,
                                contentColor = BackgroundPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Install", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // Manual import as fallback (collapsed section)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(title = "Manual Import")
        }

        item {
            OutlinedButton(
                onClick = { filePickerLauncher.launch("*/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.FileUpload,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = TextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Import from CSV file", color = TextSecondary)
            }
        }

        item {
            Text(
                "For countries not listed above, download CSV files from opencellid.org",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}
