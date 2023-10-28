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
    alias(libs.plugins.lastaapps.android.library.core)
    alias(libs.plugins.sqldelight)
}

sqldelight {
    databases {
        create("CrashDatabase") {
            packageName.set("cz.lastaapps.crash")
            schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
            verifyMigrations.set(true)
        }
    }
}

android {
    namespace = "cz.lastaapps.crash"
}

dependencies {
    implementation(libs.androidx.startup)

    implementation(libs.sqldelight.android)
    implementation(libs.sqldelight.runtime)
    implementation(libs.sqldelight.coroutines)
}
