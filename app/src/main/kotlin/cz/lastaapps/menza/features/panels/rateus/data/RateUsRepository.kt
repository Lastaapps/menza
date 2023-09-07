/*
 *    Copyright 2023, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.features.panels.rateus.data

import kotlin.time.Duration.Companion.days
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

internal interface RateUsRepository {
    fun shouldShow(): Flow<Boolean>
    suspend fun dismissPermanently()
    suspend fun showLater()
    suspend fun appOpened()
}

internal class RateUsRepositoryImpl(
    private val source: RateUsDataSource,
    private val clock: Clock,
) : RateUsRepository {
    override fun shouldShow(): Flow<Boolean> =
        combine(
            source.getShouldRate()
                .map { instant ->
                    instant?.let { it < clock.now() } ?: false
                },
            source.isDisabled(),
        ) { shouldShow, disabled ->
            shouldShow && disabled != true
        }

    override suspend fun dismissPermanently() {
        source.setDisabled(true)
    }

    override suspend fun showLater() {
        source.setShouldRate(clock.now() + SHOULD_RATE_AFTER)
    }

    override suspend fun appOpened() {
        source.getShouldRate().first() ?: run {
            source.setShouldRate(clock.now() + SHOULD_RATE_AFTER)
        }
    }

    companion object {
        private val SHOULD_RATE_AFTER = 7.days
    }
}
