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

package com.bp22intel.edgesentinel.data.local

import androidx.room.TypeConverter
import com.bp22intel.edgesentinel.domain.model.Confidence
import com.bp22intel.edgesentinel.domain.model.NetworkType
import com.bp22intel.edgesentinel.domain.model.ThreatLevel
import com.bp22intel.edgesentinel.domain.model.ThreatType

class Converters {

    @TypeConverter
    fun fromNetworkType(value: NetworkType): String = value.name

    @TypeConverter
    fun toNetworkType(value: String): NetworkType =
        NetworkType.valueOf(value)

    @TypeConverter
    fun fromThreatType(value: ThreatType): String = value.name

    @TypeConverter
    fun toThreatType(value: String): ThreatType =
        ThreatType.valueOf(value)

    @TypeConverter
    fun fromThreatLevel(value: ThreatLevel): String = value.name

    @TypeConverter
    fun toThreatLevel(value: String): ThreatLevel =
        ThreatLevel.valueOf(value)

    @TypeConverter
    fun fromConfidence(value: Confidence): String = value.name

    @TypeConverter
    fun toConfidence(value: String): Confidence =
        Confidence.valueOf(value)
}
