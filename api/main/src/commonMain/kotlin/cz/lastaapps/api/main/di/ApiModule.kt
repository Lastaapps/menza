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

package cz.lastaapps.api.main.di

import cz.lastaapps.api.buffet.di.apiBuffetModule
import cz.lastaapps.api.core.di.apiCoreModule
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.repo.MenzaRepo
import cz.lastaapps.api.main.data.MenzaMasterRepoImpl
import cz.lastaapps.api.main.data.WalletMasterRepository
import cz.lastaapps.api.main.data.WalletMasterRepositoryImpl
import cz.lastaapps.api.main.domain.usecase.GetImportantRequestParams
import cz.lastaapps.api.main.domain.usecase.GetInfoUC
import cz.lastaapps.api.main.domain.usecase.GetMenzaListUC
import cz.lastaapps.api.main.domain.usecase.GetTodayDishListUC
import cz.lastaapps.api.main.domain.usecase.GetWeekDishListUC
import cz.lastaapps.api.main.domain.usecase.OpenMenuUC
import cz.lastaapps.api.main.domain.usecase.SyncAllInfoUC
import cz.lastaapps.api.main.domain.usecase.SyncInfoUC
import cz.lastaapps.api.main.domain.usecase.SyncMenzaListUC
import cz.lastaapps.api.main.domain.usecase.SyncTodayDishListUC
import cz.lastaapps.api.main.domain.usecase.SyncWeekDishListUC
import cz.lastaapps.api.main.domain.usecase.wallet.WalletGetBalanceUC
import cz.lastaapps.api.main.domain.usecase.wallet.WalletLoginUC
import cz.lastaapps.api.main.domain.usecase.wallet.WalletLogoutUC
import cz.lastaapps.api.main.domain.usecase.wallet.WalletRefreshUC
import cz.lastaapps.menza.api.agata.di.apiAgataModule
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.module

val apiModule = module {
    includes(
        apiAgataModule,
        apiBuffetModule,
        apiCoreModule,
    )

    val rootName = named<MenzaMasterRepoImpl>()
    single(rootName) { MenzaMasterRepoImpl() } bind MenzaRepo::class
    singleOf(::WalletMasterRepositoryImpl) bind WalletMasterRepository::class

    factory { GetMenzaListUC(get(), get(rootName), get()) }
    factory { SyncMenzaListUC(get(), get(rootName), get(), get()) }
    factoryOf(::GetInfoUC)
    factoryOf(::SyncInfoUC)
    factoryOf(::SyncAllInfoUC)
    factoryOf(::GetTodayDishListUC)
    factoryOf(::SyncTodayDishListUC)
    factoryOf(::GetWeekDishListUC)
    factoryOf(::SyncWeekDishListUC)
    factoryOf(::OpenMenuUC)
    factoryOf(::WalletGetBalanceUC)
    factoryOf(::WalletLoginUC)
    factoryOf(::WalletLogoutUC)
    factoryOf(::WalletRefreshUC)
    factoryOf(::GetImportantRequestParams)
}

private fun Scope.MenzaMasterRepoImpl() =
    MenzaType.allNamed.map { name ->
        get<MenzaRepo>(name)
    }.let {
        MenzaMasterRepoImpl(it)
    }
