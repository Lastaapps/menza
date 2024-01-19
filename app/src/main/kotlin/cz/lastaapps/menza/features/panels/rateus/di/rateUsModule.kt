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

package cz.lastaapps.menza.features.panels.rateus.di

import com.russhwolf.settings.ExperimentalSettingsApi
import cz.lastaapps.menza.features.panels.rateus.data.RateUsDataSource
import cz.lastaapps.menza.features.panels.rateus.data.RateUsDataSourceImpl
import cz.lastaapps.menza.features.panels.rateus.data.RateUsRepository
import cz.lastaapps.menza.features.panels.rateus.data.RateUsRepositoryImpl
import cz.lastaapps.menza.features.panels.rateus.data.RateUsStore
import cz.lastaapps.menza.features.panels.rateus.domain.usecase.DismissRateUsUC
import cz.lastaapps.menza.features.panels.rateus.domain.usecase.RecordAppOpenedUC
import cz.lastaapps.menza.features.panels.rateus.domain.usecase.ShouldShowRateUsUC
import cz.lastaapps.menza.features.panels.rateus.ui.RateUsViewModel

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val rateUsModule = module {
    factoryOf(::RateUsViewModel)

    single { RateUsStore.create(get()) }
    @OptIn(ExperimentalSettingsApi::class)
    factoryOf(::RateUsDataSourceImpl) bind RateUsDataSource::class
    singleOf(::RateUsRepositoryImpl) bind RateUsRepository::class

    factoryOf(::DismissRateUsUC)
    factoryOf(::RecordAppOpenedUC)
    factoryOf(::ShouldShowRateUsUC)
}
