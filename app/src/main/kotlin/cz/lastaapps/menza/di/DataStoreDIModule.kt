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
import cz.lastaapps.menza.ui.dests.others.privacy.PrivacyStore
import cz.lastaapps.menza.ui.dests.settings.store.SettingsStore
import cz.lastaapps.menza.ui.layout.menza.MenzaOrderDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreDIModule {

    @Provides
    @Singleton
    fun provideSettingsDataStore(app: Application): SettingsStore {
        return SettingsStore(app, CoroutineScope(Dispatchers.Default))
    }

    @Provides
    @Singleton
    fun providePrivacyDataStore(app: Application): PrivacyStore {
        return PrivacyStore(app)
    }

    @Provides
    @Singleton
    fun provideMenzaOrderDataStore(app: Application): MenzaOrderDataStore {
        return MenzaOrderDataStore(app)
    }

}
