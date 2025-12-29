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
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

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
                with(sourceSets) {
                    commonMain.dependencies {
                        dependenciesCoroutines()
                    }
                    commonTest.dependencies {
                        dependenciesCoroutinesTest()
                    }
                    androidMain.dependencies {
                        dependenciesCoroutinesAndroid()
                    }
                }
            }
        },
    ) {
    companion object {
        context(p: Project)
        fun KotlinDependencyHandler.dependenciesCoroutines() {
            implementation(p.libs.kotlinx.coroutines.common)
        }

        context(p: Project)
        fun KotlinDependencyHandler.dependenciesCoroutinesAndroid() {
            implementation(p.libs.kotlinx.coroutines.android)
        }

        context(p: Project)
        fun KotlinDependencyHandler.dependenciesCoroutinesTest() {
            implementation(p.libs.kotlinx.coroutines.test)
        }
    }
}
