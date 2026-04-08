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

package com.bp22intel.edgesentinel.di

import com.bp22intel.edgesentinel.detection.detectors.CipherModeDetector
import com.bp22intel.edgesentinel.detection.detectors.FakeBtsDetector
import com.bp22intel.edgesentinel.detection.detectors.NetworkDowngradeDetector
import com.bp22intel.edgesentinel.detection.detectors.SilentSmsDetector
import com.bp22intel.edgesentinel.detection.detectors.ThreatDetector
import com.bp22intel.edgesentinel.detection.detectors.TrackingPatternDetector
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class DetectionModule {

    @Binds
    @IntoSet
    abstract fun bindFakeBtsDetector(impl: FakeBtsDetector): ThreatDetector

    @Binds
    @IntoSet
    abstract fun bindNetworkDowngradeDetector(impl: NetworkDowngradeDetector): ThreatDetector

    @Binds
    @IntoSet
    abstract fun bindSilentSmsDetector(impl: SilentSmsDetector): ThreatDetector

    @Binds
    @IntoSet
    abstract fun bindTrackingPatternDetector(impl: TrackingPatternDetector): ThreatDetector

    @Binds
    @IntoSet
    abstract fun bindCipherModeDetector(impl: CipherModeDetector): ThreatDetector
}
