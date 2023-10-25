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

plugins {
    alias(libs.plugins.lastaapps.kmp.library)
    alias(libs.plugins.kotlin.atomicfu)
}

android {
    namespace = "cz.lastaapps.api.core"
}

dependencies {
    commonMainImplementation(projects.core)

    commonMainImplementation(libs.kotlinx.atomicfu)
    commonMainImplementation(libs.sqldelight.runtime)
    commonMainImplementation(libs.bundles.russhwolf.settings)

    androidMainImplementation(libs.androidx.security)
    androidMainImplementation(libs.androidx.datastore)
}
