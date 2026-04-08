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

package com.bp22intel.edgesentinel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bp22intel.edgesentinel.ui.theme.EdgeSentinelTheme
import com.bp22intel.edgesentinel.ui.theme.StatusClear
import com.bp22intel.edgesentinel.ui.theme.StatusSuspicious
import com.bp22intel.edgesentinel.ui.theme.StatusThreat
import com.bp22intel.edgesentinel.ui.theme.TextTertiary

/**
 * Converts a dBm signal strength value to a 0-4 bar representation.
 * Typical ranges: -50 dBm (excellent) to -120 dBm (no signal).
 */
private fun dBmToBars(dBm: Int): Int {
    return when {
        dBm >= -70 -> 4
        dBm >= -85 -> 3
        dBm >= -100 -> 2
        dBm >= -110 -> 1
        else -> 0
    }
}

@Composable
fun SignalStrengthBar(
    dBm: Int,
    modifier: Modifier = Modifier
) {
    val bars = dBmToBars(dBm)
    val activeColor = when (bars) {
        4, 3 -> StatusClear
        2 -> StatusSuspicious
        else -> StatusThreat
    }
    val barHeights = listOf(6.dp, 10.dp, 14.dp, 18.dp)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        for (i in 0 until 4) {
            val isActive = i < bars
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(barHeights[i])
                    .clip(RoundedCornerShape(1.dp))
                    .background(if (isActive) activeColor else TextTertiary)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
private fun SignalStrengthBarPreview() {
    EdgeSentinelTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SignalStrengthBar(dBm = -60)
            SignalStrengthBar(dBm = -90)
            SignalStrengthBar(dBm = -115)
        }
    }
}
