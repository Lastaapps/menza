/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.core.util.providers

import cz.lastaapps.core.domain.Outcome

interface LinkOpener {
    fun openLink(url: String): Outcome<Unit>

    fun writeEmail(
        emails: List<String>,
        subject: String?,
        content: String?,
    ): Outcome<Unit>

    fun callPhoneNumber(number: String): Outcome<Unit>

    fun openAddress(address: String): Outcome<Unit>

    fun openGeo(
        lat: Float,
        long: Float,
    ): Outcome<Unit>

    fun openTelegram(groupUrl: String): Outcome<Unit>

    fun openFacebookPage(pageUrl: String): Outcome<Unit>
}

fun LinkOpener.writeEmail(
    email: String,
    subject: String?,
    content: String?,
) = writeEmail(listOf(email), subject, content)
