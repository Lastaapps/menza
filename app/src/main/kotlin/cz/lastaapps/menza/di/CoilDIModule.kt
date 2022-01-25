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

package cz.lastaapps.menza.di

import android.app.Application
import coil.ImageLoader
import coil.request.CachePolicy
import coil.util.DebugLogger
import cz.lastaapps.menza.CacheHeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoilDIModule {

    @Provides
    @Singleton
    fun provideSettingsDataStore(app: Application): ImageLoader {
        return ImageLoader.Builder(app)
            .crossfade(true)
            .networkCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .networkObserverEnabled(true)
            .respectCacheHeaders(true)
            .logger(DebugLogger())
            .okHttpClient {
                OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    //.readTimeout(10, TimeUnit.SECONDS)
                    .addNetworkInterceptor(CacheHeaderInterceptor)
                    .retryOnConnectionFailure(false)
                    .build()
            }
            .build()
    }

}

