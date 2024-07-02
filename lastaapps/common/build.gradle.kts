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

import java.time.LocalDate
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.lastaapps.android.library)
    alias(libs.plugins.lastaapps.common.compose)
    `maven-publish`
}

android {
    namespace = "cz.lastaapps.common"

    defaultConfig {
        val buildDateTime = LocalDate.now(UTC).format(DateTimeFormatter.ISO_DATE)
        buildConfigField("java.lang.String", "BUILD_DATE", "\"$buildDateTime\"")
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.google.material)
}
