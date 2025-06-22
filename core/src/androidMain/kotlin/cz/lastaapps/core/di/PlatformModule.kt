/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.core.di

import cz.lastaapps.core.data.AndroidAssetsProvider
import cz.lastaapps.core.data.AndroidDeviceLocalesProvider
import cz.lastaapps.core.data.AssetsProvider
import cz.lastaapps.core.data.CryptoProvider
import cz.lastaapps.core.data.DeviceLocalesProvider
import cz.lastaapps.core.data.DummyCryptoProvider
import cz.lastaapps.core.data.IsOnMeteredNetworkProvider
import cz.lastaapps.core.data.IsOnMeteredNetworkProviderImpl
import cz.lastaapps.core.data.JavaXCryptoProvider
import cz.lastaapps.core.data.createSettings
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.util.providers.AndroidLinkOpener
import cz.lastaapps.core.util.providers.LinkOpener
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platform: Module =
    module {
        single { createSettings(get()) }
        single { VMContext(Dispatchers.Default) }

        factory<CryptoProvider> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                JavaXCryptoProvider()
            } else {
                DummyCryptoProvider()
            }
        }

        factoryOf(::IsOnMeteredNetworkProviderImpl) bind IsOnMeteredNetworkProvider::class
        factoryOf(::AndroidLinkOpener) bind LinkOpener::class
        factoryOf(::AndroidAssetsProvider) bind AssetsProvider::class
        factoryOf(::AndroidDeviceLocalesProvider) bind DeviceLocalesProvider::class
    }
