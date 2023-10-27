/*
 *    Copyright 2023, Petr Laštovička as Lasta apps, All rights reserved
 *
 *     This file is part of Menza.
 *
 *     Menza is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Menza is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Menza.  If not, see <https://www.gnu.org/licenses/>.
 */

package cz.lastaapps.core.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService
import cz.lastaapps.core.util.extensions.localLogger
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class IsOnMeteredNetworkProviderImpl(
    private val context: Context,
) : IsOnMeteredNetworkProvider {

    companion object {
        private val log = localLogger()
    }

    override fun isOnMeteredNetwork(): Flow<Boolean> {
        val manager =
            getSystemService(context, ConnectivityManager::class.java)
                ?: return flow { emit(false) }

        val network =
            if (Build.VERSION.SDK_INT >= 23) {
                manager.activeNetwork
            } else {
                null
            }

        return callbackFlow {
            send(manager.getNetworkCapabilities(network))

            val request = NetworkRequest.Builder().build()
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities,
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    trySend(networkCapabilities)
                }
            }
            manager.registerNetworkCallback(request, callback)

            awaitClose {
                manager.unregisterNetworkCallback(callback)
            }
        }.map {
            it?.isMetered() ?: false
        }.onEach {
            log.i { "Metered state: $it" }
        }
    }

    private fun NetworkCapabilities.isMetered(): Boolean =
        !hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
}
