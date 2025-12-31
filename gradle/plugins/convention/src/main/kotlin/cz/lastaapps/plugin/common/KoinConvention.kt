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

package cz.lastaapps.plugin.common

import cz.lastaapps.extensions.implementation
import cz.lastaapps.extensions.libs
import cz.lastaapps.extensions.multiplatform
import cz.lastaapps.extensions.testImplementation
import cz.lastaapps.plugin.BasePlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

private const val ENABLE_ANNOTATIONS = true

class KoinConvention :
    BasePlugin(
        configuration = {
            if (ENABLE_ANNOTATIONS) {
                apply<KspConvention>()
            }
        },
        kmpConfiguration = {
            multiplatform {
                sourceSets.commonMain.dependencies {
                    implementation(libs.koin.core)

                    if (ENABLE_ANNOTATIONS) {
                        implementation(libs.koin.annotations.asProvider())
                    }
                }
                sourceSets.androidMain.dependencies {
                    implementation(libs.koin.android.core)
                }
                sourceSets.commonTest.dependencies {
                    implementation(libs.koin.test.jUnit5)
                }
            }
            dependencies {
                if (ENABLE_ANNOTATIONS) {
                    // apply KSP compiler plugin on all possible targets
                    configurations
                        .filter { it.name.startsWith("ksp") && it.name != "ksp" }
                        .forEach {
                            // "ksp" for Android, "kspCommonMainMetadata", "kspAndroid", "kspJvm"
                            add(it.name, libs.koin.annotations.compiler)
                        }
                }
            }
        },
        androidConfiguration = {
            dependencies {
                implementation(libs.koin.core)

                if (ENABLE_ANNOTATIONS) {
                    implementation(libs.koin.annotations.asProvider())
                }
                implementation(libs.koin.android.core)
                testImplementation(libs.koin.test.jUnit5)

                if (ENABLE_ANNOTATIONS) {
                    add("ksp", libs.koin.annotations.compiler)
                }
            }
        },
    )
