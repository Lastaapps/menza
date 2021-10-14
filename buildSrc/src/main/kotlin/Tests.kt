/*
 *    Copyright 2021, Petr Laštovička as Lasta apps, All rights reserved
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

object Tests {

    const val ROOM = "androidx.room:room-testing:${Versions.ROOM}"

    const val COMPOSE = "androidx.compose.ui:ui-test-junit4:${Versions.COMPOSE}"

    const val KOTEST_ASSERTION = "io.kotest:kotest-assertions-core:${Versions.KOTEST}"

    //android
    const val JUNIT = "junit:junit:${Versions.TEST_JUNIT}"
    const val ARCH = "androidx.arch.core:core-testing:${Versions.TEST_ARCH}"
    const val CORE = "androidx.test:core-ktx:${Versions.TEST_ANDROIDX}"
    const val RUNNER = "androidx.test:runner:${Versions.TEST_ANDROIDX}"
    const val RULES = "androidx.test:rules:${Versions.TEST_ANDROIDX}"
    const val TRUTH = "androidx.test.ext:truth:${Versions.TEST_ANDROIDX}"
    const val ANDROIDX_JUNIT = "androidx.test.ext:junit-ktx:${Versions.TEST_ANDROIDX_JUNIT}"
    const val GOOGLE_TRUTH = "com.google.truth:truth${Versions.TEST_GOOGLE_TRUTH}"
    const val COROUTINES =
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.TEST_KOTLIN_COROUTINES}"
    const val ROBOELECTRIC = "org.robolectric:robolectric:${Versions.TEST_ROBOELECTRIC}"
    const val ESPRESSO = "androidx.test.espresso:espresso-core:${Versions.TEST_ESPRESSO_CORE}"

}