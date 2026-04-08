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

package com.bp22intel.edgesentinel.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EdgeSentinelColorScheme = darkColorScheme(
    primary = StatusClear,
    secondary = AccentBlue,
    tertiary = StatusSuspicious,
    error = StatusThreat,
    background = BackgroundPrimary,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    onPrimary = BackgroundPrimary,
    onSecondary = BackgroundPrimary,
    onTertiary = BackgroundPrimary,
    onError = BackgroundPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = TextTertiary
)

@Composable
fun EdgeSentinelTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EdgeSentinelColorScheme,
        typography = EdgeSentinelTypography,
        shapes = EdgeSentinelShapes,
        content = content
    )
}
