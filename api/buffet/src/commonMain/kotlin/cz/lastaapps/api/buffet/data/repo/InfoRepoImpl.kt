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

package cz.lastaapps.api.buffet.data.repo

import arrow.core.right
import cz.lastaapps.api.buffet.domain.model.BuffetType
import cz.lastaapps.api.buffet.domain.model.BuffetType.FEL
import cz.lastaapps.api.buffet.domain.model.BuffetType.FS
import cz.lastaapps.api.core.domain.model.common.Contact
import cz.lastaapps.api.core.domain.model.common.Info
import cz.lastaapps.api.core.domain.model.common.Link
import cz.lastaapps.api.core.domain.model.common.OpeningTime
import cz.lastaapps.api.core.domain.model.common.PlaceOpeningTime
import cz.lastaapps.api.core.domain.repo.InfoRepo
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncResult
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

internal class InfoRepoImpl(
    private val type: BuffetType,
) : InfoRepo {
    override fun getData(): Flow<Info> = flow {
        // I don't wanna parse this shit, really
        emit(
            Info(
                header = null,
                footer = null,
                contacts = commonContacts,
                openingTimes = openTime(type),
                links = persistentListOf(
                    Link(
                        link = "studentcatering@seznam.cz",
                        description = "Web",
                    ),
                ),
                gps = null,
                address = address(type),
            )
        )
    }

    @Suppress("SpellCheckingInspection")
    private fun openTime(type: BuffetType) = persistentListOf(
        PlaceOpeningTime(
            when (type) {
                FS -> "FS Bufet"
                FEL -> "FEL Bufet"
            },
            when (type) {
                FS -> "FS"
                FEL -> "FEL"
            },
            null,
            persistentListOf(
                OpeningTime(
                    from = DayOfWeek.MONDAY to LocalTime(7, 45),
                    to = DayOfWeek.THURSDAY to LocalTime(16, 45),
                ),
                OpeningTime(
                    from = DayOfWeek.FRIDAY to LocalTime(7, 45),
                    to = DayOfWeek.FRIDAY to LocalTime(14, 0),
                ),
            )
        )
    )

    @Suppress("SpellCheckingInspection")
    private fun address(type: BuffetType) = when (type) {
        FS -> "1. partro, Technická 1902/4, 160 00 Praha 6"
        FEL -> "1. partro, Technická 1902/2, 160 00 Praha 6"
    }

    @Suppress("SpellCheckingInspection")
    private val commonContacts =
        persistentListOf(
            Contact(
                role = "Provoz",
                name = null,
                phone = "+420 224 352 064",
                email = null,
            ),
            Contact(
                role = "jednatel",
                name = "Ing. Michal Janča",
                phone = "+420 602 447 080",
                email = null,
            ),
            Contact(
                role = null,
                name = null,
                phone = null,
                email = "studentcatering@seznam.cz",
            ),
        )

    override suspend fun sync(isForced: Boolean): SyncOutcome = SyncResult.Skipped.right()
}