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

package cz.lastaapps.plugin.android.config

import com.android.build.api.dsl.CommonExtension
import cz.lastaapps.extensions.alias
import cz.lastaapps.extensions.debugImplementation
import cz.lastaapps.extensions.implementation
import cz.lastaapps.extensions.libs
import cz.lastaapps.extensions.pluginManager
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies


internal fun Project.configureComposeCompiler(
    commonExtension: CommonExtension<*, *, *, *, *>,
) = with(commonExtension) {
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }
}

internal fun Project.configureComposeDependencies() {
    pluginManager {
        // Required by Decompose
        alias(libs.plugins.kotlin.serialization)
    }

    dependencies {

        implementation(libs.google.material)

        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.constraintlayout.compose)
        implementation(libs.androidx.compose.material3)
        implementation(libs.androidx.compose.material3WindowSizeClass)
        implementation(libs.androidx.compose.iconsCore)
        implementation(libs.androidx.compose.iconsExtended)
        implementation(libs.androidx.compose.animation)
        implementation(libs.androidx.compose.ui.util)
        debugImplementation(libs.androidx.compose.tooling)
        implementation(libs.androidx.compose.toolingPreview)

        implementation(libs.androidx.lifecycle.runtime.asProvider())
        implementation(libs.androidx.lifecycle.runtime.compose)

        implementation(libs.decompose.core)
        implementation(libs.decompose.compose)

        implementation(libs.coil.compose.complete)
    }
}
