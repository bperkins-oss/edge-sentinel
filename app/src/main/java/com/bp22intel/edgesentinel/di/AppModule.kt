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

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.bp22intel.edgesentinel.data.local.EdgeSentinelDatabase
import com.bp22intel.edgesentinel.data.local.dao.AlertDao
import com.bp22intel.edgesentinel.data.local.dao.CellDao
import com.bp22intel.edgesentinel.data.local.dao.ScanDao
import com.bp22intel.edgesentinel.data.sensor.CellInfoCollector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): EdgeSentinelDatabase {
        return Room.databaseBuilder(
            context,
            EdgeSentinelDatabase::class.java,
            "edge_sentinel.db"
        ).build()
    }

    @Provides
    fun provideCellDao(db: EdgeSentinelDatabase): CellDao = db.cellDao()

    @Provides
    fun provideAlertDao(db: EdgeSentinelDatabase): AlertDao = db.alertDao()

    @Provides
    fun provideScanDao(db: EdgeSentinelDatabase): ScanDao = db.scanDao()

    @Provides
    @Singleton
    fun provideCellInfoCollector(
        @ApplicationContext context: Context
    ): CellInfoCollector {
        return CellInfoCollector(context)
    }

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }
}
