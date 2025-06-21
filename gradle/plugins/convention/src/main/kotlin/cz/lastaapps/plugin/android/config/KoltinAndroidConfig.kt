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

package cz.lastaapps.plugin.android.config

import com.android.build.api.dsl.CommonExtension
import cz.lastaapps.extensions.compilerOptions
import cz.lastaapps.extensions.coreLibraryDesugaring
import cz.lastaapps.extensions.implementation
import cz.lastaapps.extensions.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) =
    with(commonExtension) {
        compileSdk =
            libs.versions.sdk.compile
                .get()
                .toInt()

        defaultConfig {
            minSdk =
                libs.versions.sdk.min
                    .get()
                    .toInt()
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compilerOptions {
            // Treat all Kotlin warnings as errors (disabled by default)
            allWarningsAsErrors = properties["warningsAsErrors"] as? Boolean ?: false

            freeCompilerArgs.addAll(
                listOf(
                    "-opt-in=kotlin.RequiresOptIn",
                    // enforce Java nullability
                    "-Xjspecify-annotations=strict",
                    "-Xtype-enhancement-improvements-strict-mode",
                ),
            )
        }

        compileOptions {
            val versionCode =
                libs.versions.java.jvmTarget
                    .get()
                    .toInt()
            val version = JavaVersion.toVersion(versionCode)
            sourceCompatibility = version
            targetCompatibility = version
            isCoreLibraryDesugaringEnabled = true
        }

        dependencies {
            coreLibraryDesugaring(libs.android.desugaring)

            implementation(platform(libs.kotlin.bom))
        }
    }
