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

package cz.lastaapps.api.core.domain.model

import org.koin.core.qualifier.named

// All the types must have unique names, or the DI will break
sealed interface MenzaType {
    val id: String

    companion object {
        val allNamed = listOf(
            named<Agata.Strahov>(),
            named<Agata.Subsystem>(),
            named<Buffet.FS>(),
            named<Buffet.FEL>(),
            named<Testing.Kocourkov>(),
        )
    }

    sealed interface Agata : MenzaType {
        data class Subsystem(val subsystemId: Int) : Agata {
            override val id: String = "agata_subsystem_$subsystemId"
        }

        data object Strahov : Agata {
            override val id: String = "agata_strahov"
        }
    }

    sealed interface Buffet : MenzaType {
        data object FS : Buffet {
            override val id: String = "buffet_fs"
        }

        data object FEL : Buffet {
            override val id: String = "buffet_fel"
        }
    }

    sealed interface Testing : MenzaType {
        data object Kocourkov : Testing {
            override val id: String = "kocourkov"
        }
    }
}
