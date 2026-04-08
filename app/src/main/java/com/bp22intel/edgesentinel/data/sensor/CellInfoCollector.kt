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

package com.bp22intel.edgesentinel.data.sensor

import android.content.Context
import android.telephony.CellInfoCdma
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoNr
import android.telephony.CellInfoWcdma
import android.telephony.TelephonyManager
import com.bp22intel.edgesentinel.domain.model.CellTower
import com.bp22intel.edgesentinel.domain.model.NetworkType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CellInfoCollector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val telephonyManager: TelephonyManager
        get() = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    @Suppress("MissingPermission")
    fun getCurrentCellInfo(): List<CellTower> {
        val now = System.currentTimeMillis()
        val cellInfoList = try {
            telephonyManager.allCellInfo ?: emptyList()
        } catch (e: SecurityException) {
            emptyList()
        }

        return cellInfoList.mapNotNull { cellInfo ->
            when (cellInfo) {
                is CellInfoLte -> {
                    val identity = cellInfo.cellIdentity
                    val signal = cellInfo.cellSignalStrength
                    CellTower(
                        id = 0,
                        cid = identity.ci,
                        lacTac = identity.tac,
                        mcc = identity.mccString?.toIntOrNull() ?: 0,
                        mnc = identity.mncString?.toIntOrNull() ?: 0,
                        signalStrength = signal.dbm,
                        networkType = NetworkType.LTE,
                        latitude = null,
                        longitude = null,
                        firstSeen = now,
                        lastSeen = now,
                        timesSeen = 1
                    )
                }
                is CellInfoNr -> {
                    val identity = cellInfo.cellIdentity as android.telephony.CellIdentityNr
                    val signal = cellInfo.cellSignalStrength
                    CellTower(
                        id = 0,
                        cid = identity.nci.toInt(),
                        lacTac = identity.tac,
                        mcc = identity.mccString?.toIntOrNull() ?: 0,
                        mnc = identity.mncString?.toIntOrNull() ?: 0,
                        signalStrength = signal.dbm,
                        networkType = NetworkType.NR,
                        latitude = null,
                        longitude = null,
                        firstSeen = now,
                        lastSeen = now,
                        timesSeen = 1
                    )
                }
                is CellInfoGsm -> {
                    val identity = cellInfo.cellIdentity
                    val signal = cellInfo.cellSignalStrength
                    CellTower(
                        id = 0,
                        cid = identity.cid,
                        lacTac = identity.lac,
                        mcc = identity.mccString?.toIntOrNull() ?: 0,
                        mnc = identity.mncString?.toIntOrNull() ?: 0,
                        signalStrength = signal.dbm,
                        networkType = NetworkType.GSM,
                        latitude = null,
                        longitude = null,
                        firstSeen = now,
                        lastSeen = now,
                        timesSeen = 1
                    )
                }
                is CellInfoWcdma -> {
                    val identity = cellInfo.cellIdentity
                    val signal = cellInfo.cellSignalStrength
                    CellTower(
                        id = 0,
                        cid = identity.cid,
                        lacTac = identity.lac,
                        mcc = identity.mccString?.toIntOrNull() ?: 0,
                        mnc = identity.mncString?.toIntOrNull() ?: 0,
                        signalStrength = signal.dbm,
                        networkType = NetworkType.WCDMA,
                        latitude = null,
                        longitude = null,
                        firstSeen = now,
                        lastSeen = now,
                        timesSeen = 1
                    )
                }
                is CellInfoCdma -> {
                    val identity = cellInfo.cellIdentity
                    val signal = cellInfo.cellSignalStrength
                    CellTower(
                        id = 0,
                        cid = identity.basestationId,
                        lacTac = identity.networkId,
                        mcc = 0,
                        mnc = identity.systemId,
                        signalStrength = signal.dbm,
                        networkType = NetworkType.CDMA,
                        latitude = identity.latitude.toDouble(),
                        longitude = identity.longitude.toDouble(),
                        firstSeen = now,
                        lastSeen = now,
                        timesSeen = 1
                    )
                }
                else -> null
            }
        }
    }

    @Suppress("MissingPermission")
    fun getSignalStrength(): Int {
        return try {
            val cellInfoList = telephonyManager.allCellInfo ?: emptyList()
            cellInfoList.firstOrNull { it.isRegistered }
                ?.cellSignalStrength?.dbm ?: Int.MIN_VALUE
        } catch (e: SecurityException) {
            Int.MIN_VALUE
        }
    }

    @Suppress("MissingPermission")
    fun getNetworkType(): NetworkType {
        return try {
            when (telephonyManager.dataNetworkType) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE -> NetworkType.GSM
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_EHRPD -> NetworkType.CDMA
                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPAP -> NetworkType.WCDMA
                TelephonyManager.NETWORK_TYPE_LTE -> NetworkType.LTE
                TelephonyManager.NETWORK_TYPE_NR -> NetworkType.NR
                else -> NetworkType.UNKNOWN
            }
        } catch (e: SecurityException) {
            NetworkType.UNKNOWN
        }
    }
}
