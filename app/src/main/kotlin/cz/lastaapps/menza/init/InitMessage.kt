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

package cz.lastaapps.menza.init

import androidx.annotation.StringRes
import cz.lastaapps.menza.R

sealed class InitMessage private constructor(@StringRes val message: Int) {
    object Preparing : InitMessage(R.string.init_message_preparing)
    object Done : InitMessage(R.string.init_message_done)
    object Error : InitMessage(R.string.init_message_error)

    object AllergenDone : InitMessage(R.string.init_message_allergen)
    object ContactsDone : InitMessage(R.string.init_message_contacts)
    object LocationDone : InitMessage(R.string.init_message_location)
    object MenzaDone : InitMessage(R.string.init_message_menza)
    object MessageDone : InitMessage(R.string.init_message_message)
    object OpeningHoursDone : InitMessage(R.string.init_message_opening_hours)
}
