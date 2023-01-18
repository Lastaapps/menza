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

package cz.lastaapps.menza.api.agata.domain.model

internal data class HashType(val func: String) {

    companion object {
        fun menuHash() =
            HashType("menus")

        fun subsystemHash() =
            HashType("subsystems")

        fun servingPacesHash(subsystemId: Int) =
            HashType("serving_places_$subsystemId")

        fun typesHash(subsystemId: Int) =
            HashType("types_$subsystemId")

        fun dishHash(subsystemId: Int) =
            HashType("dish_$subsystemId")

        fun infoHash(subsystemId: Int) =
            HashType("info_$subsystemId")

        fun openingHash(subsystemId: Int) =
            HashType("open_times_$subsystemId")

        fun contactsHash() =
            HashType("contacts")

        fun pictogramHash() =
            HashType("pictograms")

        fun linkHash(subsystemId: Int) =
            HashType("links_$subsystemId")
    }
}
