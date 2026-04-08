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
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyCallback
import android.telephony.TelephonyDisplayInfo
import android.telephony.TelephonyManager
import com.bp22intel.edgesentinel.domain.model.NetworkType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed class TelephonyEvent {
    data class NetworkDowngrade(
        val from: NetworkType,
        val to: NetworkType
    ) : TelephonyEvent()

    data class SignalStrengthChanged(
        val dbm: Int
    ) : TelephonyEvent()

    data class NetworkTypeChanged(
        val networkType: NetworkType
    ) : TelephonyEvent()
}

class TelephonyMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val telephonyManager: TelephonyManager
        get() = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    private val _events = MutableSharedFlow<TelephonyEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<TelephonyEvent> = _events.asSharedFlow()

    private val _currentNetworkType = MutableStateFlow(NetworkType.UNKNOWN)
    val currentNetworkType: StateFlow<NetworkType> = _currentNetworkType.asStateFlow()

    private val _currentSignalDbm = MutableStateFlow(Int.MIN_VALUE)
    val currentSignalDbm: StateFlow<Int> = _currentSignalDbm.asStateFlow()

    private var previousNetworkType: NetworkType = NetworkType.UNKNOWN
    private var telephonyCallback: Any? = null
    @Suppress("DEPRECATION")
    private var phoneStateListener: PhoneStateListener? = null

    @Suppress("MissingPermission")
    fun start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val callback = object :
                TelephonyCallback(),
                TelephonyCallback.DisplayInfoListener,
                TelephonyCallback.SignalStrengthsListener {

                override fun onDisplayInfoChanged(displayInfo: TelephonyDisplayInfo) {
                    val newType = mapOverrideNetworkType(displayInfo.overrideNetworkType)
                    handleNetworkTypeChange(newType)
                }

                override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                    val dbm = signalStrength.cellSignalStrengths
                        .firstOrNull()?.dbm ?: Int.MIN_VALUE
                    _currentSignalDbm.value = dbm
                    _events.tryEmit(TelephonyEvent.SignalStrengthChanged(dbm))
                }
            }
            telephonyCallback = callback
            telephonyManager.registerTelephonyCallback(
                context.mainExecutor,
                callback
            )
        } else {
            @Suppress("DEPRECATION")
            val listener = object : PhoneStateListener() {
                @Deprecated("Deprecated in Java")
                override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                    val dbm = signalStrength.cellSignalStrengths
                        .firstOrNull()?.dbm ?: Int.MIN_VALUE
                    _currentSignalDbm.value = dbm
                    _events.tryEmit(TelephonyEvent.SignalStrengthChanged(dbm))
                }
            }
            phoneStateListener = listener
            @Suppress("DEPRECATION")
            telephonyManager.listen(
                listener,
                PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
            )
        }
    }

    fun stop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (telephonyCallback as? TelephonyCallback)?.let {
                telephonyManager.unregisterTelephonyCallback(it)
            }
            telephonyCallback = null
        } else {
            @Suppress("DEPRECATION")
            phoneStateListener?.let {
                telephonyManager.listen(it, PhoneStateListener.LISTEN_NONE)
            }
            phoneStateListener = null
        }
    }

    private fun handleNetworkTypeChange(newType: NetworkType) {
        val oldType = previousNetworkType
        _currentNetworkType.value = newType
        _events.tryEmit(TelephonyEvent.NetworkTypeChanged(newType))

        if (oldType != NetworkType.UNKNOWN && isDowngrade(oldType, newType)) {
            _events.tryEmit(TelephonyEvent.NetworkDowngrade(from = oldType, to = newType))
        }
        previousNetworkType = newType
    }

    private fun isDowngrade(from: NetworkType, to: NetworkType): Boolean {
        val order = mapOf(
            NetworkType.NR to 5,
            NetworkType.LTE to 4,
            NetworkType.WCDMA to 3,
            NetworkType.CDMA to 2,
            NetworkType.GSM to 1,
            NetworkType.UNKNOWN to 0
        )
        return (order[to] ?: 0) < (order[from] ?: 0)
    }

    private fun mapOverrideNetworkType(overrideType: Int): NetworkType {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            when (overrideType) {
                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA,
                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_ADVANCED,
                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE -> NetworkType.NR
                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_CA,
                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_ADVANCED_PRO -> NetworkType.LTE
                else -> _currentNetworkType.value
            }
        } else {
            _currentNetworkType.value
        }
    }
}
