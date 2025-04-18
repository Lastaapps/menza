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

import cz.lastaapps.extensions.libs

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

plugins {
    alias(libs.plugins.lastaapps.kmp.library)
}

android {
    namespace = "cz.lastaapps.core"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    // Compose base
    androidMainImplementation(
        libs.androidx.compose.ui
            .asProvider(),
    )

    // Settings
    commonMainImplementation(libs.bundles.russhwolf.settings)

    // Ktor
    commonMainImplementation(libs.bundles.ktor.client)
    androidMainImplementation(libs.ktor.client.okhttp)
    jvmMainImplementation(libs.ktor.client.okhttp)

    // Decompose navigation
    commonMainImplementation(libs.decompose.core)

    // Lifecycle
    androidMainImplementation(libs.androidx.lifecycle.runtime)
    androidMainImplementation(libs.androidx.lifecycle.runtime.compose)
}
