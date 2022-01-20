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
import cz.lastaapps.menza.db.MenzaDatabase
import cz.lastaapps.menza.ui.settings.store.SettingsStore
import cz.lastaapps.storage.MenzaDriverFactory
import cz.lastaapps.storage.MenzaDriverFactoryFactoryImpl
import cz.lastaapps.storage.createMenzaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMenzaDatabaseDriver(app: Application): MenzaDriverFactory {
        return MenzaDriverFactoryFactoryImpl(app)
    }

    @Provides
    @Singleton
    fun provideMenzaDatabase(driver: MenzaDriverFactory): MenzaDatabase {
        return createMenzaDatabase(driver)
    }

    @Provides
    @Singleton
    fun provideSettingsDataStore(app: Application): SettingsStore {
        return SettingsStore(app, CoroutineScope(Dispatchers.Default))
    }

}