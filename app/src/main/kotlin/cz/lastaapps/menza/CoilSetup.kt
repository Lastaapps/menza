/*
 *    Copyright 2024, Petr Laštovička as Lasta apps, All rights reserved
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

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache.Builder
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.size.Precision
import coil3.util.DebugLogger
import io.ktor.client.HttpClient

internal class CoilSetup : coil3.SingletonImageLoader.Factory {
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        with(ImageLoader.Builder(context)) {
            diskCachePolicy(CachePolicy.ENABLED)
            memoryCachePolicy(CachePolicy.ENABLED)
            networkCachePolicy(CachePolicy.ENABLED)
            precision(Precision.INEXACT)

            components {
                add(KtorNetworkFetcherFactory(::HttpClient))
            }

            if (BuildConfig.DEBUG) {
                logger(DebugLogger())
            }

            diskCache {
                with(Builder()) {
                    maxSizeBytes(1024 * 1024 * 64)
                    directory(context.cacheDir.resolve("dish_image_cache"))
                    build()
                }
            }
            memoryCache {
                with(MemoryCache.Builder()) {
                    maxSizeBytes(1024 * 1024 * 32)
                    weakReferencesEnabled(true)
                    build()
                }
            }

            build()
        }
}
