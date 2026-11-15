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

plugins {
    alias(libs.plugins.lastaapps.kmp.library)
}

kotlin {
    android {
        namespace = "cz.lastaapps.core"

        androidResources {
            enable = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.russhwolf.settings)
            implementation(libs.bundles.ktor.client)
            implementation(libs.decompose.core)
        }
        androidMain.dependencies {
            implementation(
                libs.androidx.compose.ui
                    .asProvider(),
            )

            implementation(libs.ktor.client.okhttp)

            implementation(libs.androidx.lifecycle.runtime)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }
        if (false) {
            jvmMain.dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
    }
}
