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
import cz.lastaapps.api.core.domain.model.Address
import cz.lastaapps.api.core.domain.model.Contact
import cz.lastaapps.api.core.domain.model.Email
import cz.lastaapps.api.core.domain.model.Info
import cz.lastaapps.api.core.domain.model.Link
import cz.lastaapps.api.core.domain.model.LocationName
import cz.lastaapps.api.core.domain.model.PhoneNumber
import cz.lastaapps.api.core.domain.model.PlaceOpeningInfo
import cz.lastaapps.api.core.domain.model.PlaceOpeningTime
import cz.lastaapps.api.core.domain.model.PlaceOpeningType
import cz.lastaapps.api.core.domain.repo.InfoRepo
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncResult
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import org.lighthousegames.logging.logging

internal class InfoRepoImpl(
    private val type: BuffetType,
) : InfoRepo {
    private val log = logging(this::class.simpleName + "($type)")

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
                        link = "https://studentcatering.cz/",
                        description = "Web",
                    ),
                ),
                address = Address(
                    location = address(type),
                    gps = null,
                ),
            ),
        )
    }
        .onStart { log.i { "Starting collection" } }
        .onCompletion { log.i { "Completed collection" } }

    @Suppress("SpellCheckingInspection")
    private fun openTime(type: BuffetType) = persistentListOf(
        PlaceOpeningInfo(
            when (type) {
                FS -> "FS Bufet"
                FEL -> "FEL Bufet"
            },
            when (type) {
                FS -> "FS"
                FEL -> "FEL"
            },
            persistentListOf(
                PlaceOpeningType(
                    description = null,
                    times = persistentListOf(
                        PlaceOpeningTime(
                            startDay = DayOfWeek.MONDAY,
                            endDay = DayOfWeek.THURSDAY,
                            startTime = LocalTime(7, 45),
                            endTime = LocalTime(16, 45),
                        ),
                        PlaceOpeningTime(
                            startDay = DayOfWeek.FRIDAY,
                            endDay = DayOfWeek.FRIDAY,
                            startTime = LocalTime(7, 45),
                            endTime = LocalTime(14, 0),
                        ),
                    ),
                ),
            ),
        ),
    )

    @Suppress("SpellCheckingInspection")
    private fun address(type: BuffetType) = when (type) {
        FS -> "1. partro, Technická 1902/4, 160 00 Praha 6"
        FEL -> "1. partro, Technická 1902/2, 160 00 Praha 6"
    }.let(::LocationName)

    @Suppress("SpellCheckingInspection")
    private val commonContacts =
        persistentListOf(
            Contact(
                role = "Provoz",
                name = null,
                phone = "+420 224 352 064".let(::PhoneNumber),
                email = "studentcatering@seznam.cz".let(::Email),
            ),
            Contact(
                role = "jednatel",
                name = "Ing. Michal Janča",
                phone = "+420 602 447 080".let(::PhoneNumber),
                email = null,
            ),
        )

    override suspend fun sync(isForced: Boolean): SyncOutcome = run {
        log.i { "Starting sync (f: $isForced)" }
        SyncResult.Skipped.right()
    }
}
