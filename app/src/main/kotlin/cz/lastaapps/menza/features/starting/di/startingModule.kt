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

package cz.lastaapps.menza.features.starting.di

import cz.lastaapps.menza.features.settings.ui.vm.DishLanguageViewModel
import cz.lastaapps.menza.features.starting.domain.usecase.CheckDataDownloadNeededUC
import cz.lastaapps.menza.features.starting.domain.usecase.DownloadInitDataUC
import cz.lastaapps.menza.features.starting.ui.vm.AllSetViewModel
import cz.lastaapps.menza.features.starting.ui.vm.DownloadViewModel
import cz.lastaapps.menza.features.starting.ui.vm.PriceTypeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val startingModule = module {

    factoryOf(::DownloadViewModel)
    factoryOf(::DishLanguageViewModel)
    factoryOf(::AllSetViewModel)
    factoryOf(::PriceTypeViewModel)

    factoryOf(::CheckDataDownloadNeededUC)
    factoryOf(::DownloadInitDataUC)
}
