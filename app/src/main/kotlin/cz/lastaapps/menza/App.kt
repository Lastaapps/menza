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

package cz.lastaapps.menza

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

class App : Application(), ImageLoaderFactory {

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
