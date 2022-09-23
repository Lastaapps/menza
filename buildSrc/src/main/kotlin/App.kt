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

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object App {

    val buildDate: String = ZonedDateTime.now()
        .withZoneSameInstant(ZoneId.of("UTC"))
        .toLocalDate()
        .format(DateTimeFormatter.ISO_DATE)

    const val GROUP = "cz.lastaapps"
    const val APP_ID = "$GROUP.menza"

    // needs to be also updated in app/build.gradle
    const val VERSION_CODE = 1020100 // 1x major . 2x minor . 2x path . 2x build diff
    const val VERSION_NAME = "1.2.1"
    const val IS_ALPHA = false
    const val IS_BETA = false

    const val USE_PREVIEW = false
    const val MIN_SDK = 21

    // latest official version
    const val COMPILE_SDK = 33
    const val TARGET_SDK = 33

    // preview version, last released android version
    const val PREVIEW_COMPILE_SDK = 33
    const val PREVIEW_TARGET_SDK = 33
//    const val COMPILE_SDK = "android-S"
//    const val TARGET_SDK = "S"
}
