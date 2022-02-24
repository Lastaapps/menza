/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.entity.info

import cz.lastaapps.entity.menza.MenzaId

/**
 * Holds contact info to the menza administration
 * https://agata.suz.cvut.cz/jidelnicky/kontakty.php
 */
data class Contact(
    val id: MenzaId,
    val name: Name?,
    val role: Role?,
    val phoneNumber: PhoneNumber?,
    val email: Email?,
) {
    init {
        require(role != null || name != null || phoneNumber != null || email != null) {
            "None contact info passed"
        }
    }
}
