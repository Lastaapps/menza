/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza

import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import cz.lastaapps.menza.di.TodayRepoFactory
import cz.lastaapps.menza.di.TodayRepoFactoryImpl
import cz.lastaapps.menza.di.WeekRepoFactory
import cz.lastaapps.menza.di.WeekRepoFactoryImpl
import cz.lastaapps.menza.init.InitViewModel
import cz.lastaapps.menza.ui.dests.info.InfoViewModel
import cz.lastaapps.menza.ui.dests.others.crashes.CrashesViewModel
import cz.lastaapps.menza.ui.dests.others.privacy.PrivacyStore
import cz.lastaapps.menza.ui.dests.others.privacy.PrivacyViewModel
import cz.lastaapps.menza.ui.dests.others.whatsnew.WhatsNewDataStore
import cz.lastaapps.menza.ui.dests.others.whatsnew.WhatsNewViewModel
import cz.lastaapps.menza.ui.dests.settings.SettingsViewModel
import cz.lastaapps.menza.ui.dests.settings.store.SettingsStore
import cz.lastaapps.menza.ui.dests.today.AllergenViewModel
import cz.lastaapps.menza.ui.dests.today.TodayViewModel
import cz.lastaapps.menza.ui.dests.week.WeekViewModel
import cz.lastaapps.menza.ui.layout.menza.MenzaOrderDataStore
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel
import cz.lastaapps.menza.ui.root.RootViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import org.kodein.di.*
import java.util.concurrent.TimeUnit

class App : Application(), ImageLoaderFactory, DIAware {

    @Suppress("RemoveExplicitTypeArguments")
    override val di: DI by DI.lazy {
        bindProvider<Context> { this@App }
        bindProvider<App> { this@App }

        import(cz.lastaapps.crash.DIModule)
        import(cz.lastaapps.scraping.DIModule)
        import(cz.lastaapps.storage.repo.DIModule)

        bindSingleton<SettingsStore> { SettingsStore(instance(), CoroutineScope(Dispatchers.IO)) }
        bindSingleton<PrivacyStore> { PrivacyStore(instance()) }
        bindSingleton<MenzaOrderDataStore> { MenzaOrderDataStore(instance()) }
        bindSingleton<WhatsNewDataStore> { WhatsNewDataStore(instance()) }

        bindProvider<TodayRepoFactory> { TodayRepoFactoryImpl(instance()) }
        bindProvider<WeekRepoFactory> { WeekRepoFactoryImpl(instance()) }

        bindProvider<InitViewModel> {
            InitViewModel(instance(), instance(), instance(), instance(), instance(), instance())
        }
        bindProvider<InfoViewModel> {
            InfoViewModel(instance(), instance(), instance(), instance())
        }
        bindProvider<SettingsViewModel> {
            SettingsViewModel(
                instance(), instance(), instance(), instance(),
                instance(), instance(), instance(), instance(),
            )
        }
        bindProvider<MenzaViewModel> {
            MenzaViewModel(instance(), instance(), instance(), instance())
        }
        bindProvider<PrivacyViewModel> { PrivacyViewModel(instance()) }
        bindProvider<WhatsNewViewModel> { WhatsNewViewModel(instance(), instance()) }
        bindProvider<CrashesViewModel> { CrashesViewModel(instance()) }
        bindProvider<AllergenViewModel> { AllergenViewModel(instance()) }
        bindProvider<TodayViewModel> { TodayViewModel(instance(), instance()) }
        bindProvider<WeekViewModel> { WeekViewModel(instance()) }
        bindProvider<RootViewModel> { RootViewModel(instance(), instance()) }
    }

    override fun newImageLoader(): ImageLoader =
        with(ImageLoader.Builder(this)) {
            diskCachePolicy(CachePolicy.ENABLED)
            memoryCachePolicy(CachePolicy.ENABLED)
            networkCachePolicy(CachePolicy.ENABLED)
            networkObserverEnabled(true)
            if (BuildConfig.DEBUG)
                logger(DebugLogger())
            diskCache {
                with(DiskCache.Builder()) {
                    maxSizeBytes(1024 * 1024 * 32) // 32 MB
                        .directory(cacheDir.resolve("dish_image_cache"))
                    build()
                }
            }
            memoryCache {
                with(MemoryCache.Builder(this@App)) {
                    weakReferencesEnabled(true)
                    build()
                }
            }
            respectCacheHeaders(true)
            okHttpClient {
                with(OkHttpClient.Builder()) {
                    connectTimeout(5, TimeUnit.SECONDS)
                    //readTimeout(10, TimeUnit.SECONDS)
                    addNetworkInterceptor(CacheHeaderInterceptor)
                    retryOnConnectionFailure(false)
                    build()
                }
            }
            build()
        }
}