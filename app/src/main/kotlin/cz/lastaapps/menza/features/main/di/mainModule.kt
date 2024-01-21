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

package cz.lastaapps.menza.features.main.di

import cz.lastaapps.menza.features.main.data.SelectedMenzaRepoImpl
import cz.lastaapps.menza.features.main.domain.SelectedMenzaRepo
import cz.lastaapps.menza.features.main.domain.usecase.CheckLowBalanceUC
import cz.lastaapps.menza.features.main.domain.usecase.GetAddMoneyUrlUC
import cz.lastaapps.menza.features.main.domain.usecase.GetSelectedMenzaUC
import cz.lastaapps.menza.features.main.domain.usecase.IsFlipUC
import cz.lastaapps.menza.features.main.domain.usecase.SelectMenzaUC
import cz.lastaapps.menza.features.main.ui.vm.AgataWalletLoginViewModel
import cz.lastaapps.menza.features.main.ui.vm.AgataWalletViewModel
import cz.lastaapps.menza.features.main.ui.vm.MainViewModel
import cz.lastaapps.menza.features.main.ui.vm.MenzaSelectionViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mainModule = module {

    singleOf(::SelectedMenzaRepoImpl) bind SelectedMenzaRepo::class
    factoryOf(::MenzaSelectionViewModel)
    factoryOf(::MainViewModel)
    factoryOf(::AgataWalletLoginViewModel)
    factoryOf(::AgataWalletViewModel)
    factoryOf(::GetAddMoneyUrlUC)
    factoryOf(::GetSelectedMenzaUC)
    factoryOf(::SelectMenzaUC)
    factoryOf(::IsFlipUC)
    factoryOf(::CheckLowBalanceUC)
}
