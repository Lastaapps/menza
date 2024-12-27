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

package cz.lastaapps.api.rating.di

import cz.lastaapps.api.rating.api.RatingAPI
import cz.lastaapps.api.rating.api.RatingAPIImpl
import cz.lastaapps.api.rating.api.model.RatingBEConfig
import cz.lastaapps.api.rating.api.model.RatingClient
import cz.lastaapps.api.rating.data.repo.RatingRepository
import cz.lastaapps.api.rating.data.repo.RatingRepositoryImpl
import cz.lastaapps.api.rating.domain.usecase.GetDishRatingsUC
import cz.lastaapps.api.rating.domain.usecase.RateDishUC
import cz.lastaapps.api.rating.domain.usecase.SyncDishRatingsUC
import cz.lastaapps.api.rating.domain.usecase.UpdateDishRatingUC
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val apiRatingDI =
    module {
        single { RatingBEConfig.prod }
        single { RatingClient.create(get(), get()) }
        factoryOf(::RatingAPIImpl) bind RatingAPI::class
        singleOf(::RatingRepositoryImpl) bind RatingRepository::class

        factoryOf(::GetDishRatingsUC)
        factoryOf(::RateDishUC)
        factoryOf(::SyncDishRatingsUC)
        factoryOf(::UpdateDishRatingUC)
    }
