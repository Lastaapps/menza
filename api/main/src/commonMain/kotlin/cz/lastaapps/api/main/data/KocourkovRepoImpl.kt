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

package cz.lastaapps.api.main.data

import arrow.core.right
import cz.lastaapps.api.core.domain.model.Info
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.WeekDayDish
import cz.lastaapps.api.core.domain.model.dish.DishCategory
import cz.lastaapps.api.core.domain.repo.InfoRepo
import cz.lastaapps.api.core.domain.repo.InfoRepoParams
import cz.lastaapps.api.core.domain.repo.MenzaRepo
import cz.lastaapps.api.core.domain.repo.MenzaRepoParams
import cz.lastaapps.api.core.domain.repo.TodayDishRepo
import cz.lastaapps.api.core.domain.repo.TodayRepoParams
import cz.lastaapps.api.core.domain.repo.WeekDishRepo
import cz.lastaapps.api.core.domain.repo.WeekRepoParams
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncResult
import cz.lastaapps.api.core.domain.sync.SyncResult.Unavailable
import cz.lastaapps.core.data.AppInfoProvider
import cz.lastaapps.core.util.extensions.localLogger
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import org.koin.core.component.KoinComponent
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.time.Duration.Companion.milliseconds

internal class KocourkovRepoImpl(
    private val appInfoProvider: AppInfoProvider,
) : MenzaRepo {
    override fun isReady(params: MenzaRepoParams): Flow<Boolean> =
        flow {
            emit(true)
        }

    override fun getData(params: MenzaRepoParams): Flow<ImmutableList<Menza>> =
        flow {
            emit(
                persistentListOf(
                    Menza(
                        MenzaType.Testing.Kocourkov,
                        name = "Kocourkov",
                        isActive = appInfoProvider.isDebug(),
                        isOpened = true,
                        supportsDaily = true,
                        supportsWeekly = false,
                        isExperimental = false,
                        videoLinks =
                            persistentListOf(
                                "https://pointerpointer.com/images/${
                                    Random.nextInt(
                                        1..999,
                                    )
                                }.jpg",
                            ),
                    ),
                ),
            )
        }

    override suspend fun sync(
        params: MenzaRepoParams,
        isForced: Boolean,
    ): SyncOutcome {
        delay(Random.nextInt(100..1000).milliseconds)
        return SyncResult.Skipped.right()
    }
}

internal object TodayKocourkovDishRepoImpl : TodayDishRepo {
    private val dishLists =
        persistentListOf(
            DishCategory.Mock.allCathegories,
            DishCategory.Mock.empty,
        )

    private val localNotifier = MutableStateFlow(0)

    override fun getData(params: TodayRepoParams): Flow<ImmutableList<DishCategory>> =
        localNotifier.map {
            delay(Random.nextInt(100..500).milliseconds)
            dishLists[it % dishLists.size]
        }

    override suspend fun sync(
        params: TodayRepoParams,
        isForced: Boolean,
    ): SyncOutcome {
        delay(Random.nextInt(100..1000).milliseconds)
        return SyncResult.Skipped.right()
//        return if (Random.nextInt(0..3) != 0) {
//            localNotifier.value += 1
//            SyncResult.Updated.right()
//        } else {
//            SyncResult.Skipped.right()
//        }
    }
}

internal object InfoKocourkovRepoImpl : InfoRepo, KoinComponent {
    override fun getData(params: InfoRepoParams): Flow<Info> = flow { }

    override suspend fun sync(
        params: InfoRepoParams,
        isForced: Boolean,
    ): SyncOutcome = Unavailable.right()
}

internal object WeekKocourkovRepoImpl : WeekDishRepo {
    private val log = localLogger()

    override fun getData(params: WeekRepoParams): Flow<ImmutableList<WeekDayDish>> =
        flow { emit(persistentListOf<WeekDayDish>()) }
            .onStart { log.i { "Starting collection" } }
            .onCompletion { log.i { "Completed collection" } }

    override suspend fun sync(
        params: WeekRepoParams,
        isForced: Boolean,
    ): SyncOutcome =
        run {
            log.i { "Starting sync (f: $isForced)" }
            Unavailable.right()
        }
}
