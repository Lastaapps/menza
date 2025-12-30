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

package cz.lastaapps.plugin.android.common

import cz.lastaapps.extensions.compilerOptions
import cz.lastaapps.extensions.libs
import cz.lastaapps.extensions.multiplatform
import cz.lastaapps.plugin.BasePlugin
import org.gradle.api.Project

class CoroutinesConvention :
    BasePlugin(
        {
            compilerOptions {
                freeCompilerArgs.addAll(
                    listOf(
                        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                        "-opt-in=kotlinx.coroutines.FlowPreview",
                    ),
                )
            }

            multiplatform {
                sourceSets.all {
                    languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                }
                with(sourceSets) {
                    commonMain.dependencies {
                        dependenciesCoroutines().forEach(::implementation)
                    }
                    commonTest.dependencies {
                        dependenciesCoroutinesTest().forEach(::implementation)
                    }
                    androidMain.dependencies {
                        dependenciesCoroutinesAndroid().forEach(::implementation)
                    }
                }
            }
        },
    ) {
    companion object {
        fun Project.dependenciesCoroutines() = listOf(libs.kotlinx.coroutines.common)

        fun Project.dependenciesCoroutinesAndroid() = listOf(libs.kotlinx.coroutines.android)

        fun Project.dependenciesCoroutinesTest() = listOf(libs.kotlinx.coroutines.test)
    }
}
