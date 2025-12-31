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

package cz.lastaapps.plugin.multiplatform

import cz.lastaapps.plugin.BasePlugin
import cz.lastaapps.plugin.common.applyCommonConventions
import cz.lastaapps.plugin.extensions.alias
import cz.lastaapps.plugin.extensions.libs
import cz.lastaapps.plugin.extensions.multiplatform
import cz.lastaapps.plugin.extensions.pluginManager
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.withType

@Suppress("unused")
class KMPLibraryConvention :
    BasePlugin(
        {
            pluginManager {
                alias(libs.plugins.kotlin.multiplatform)
                alias(libs.plugins.kotlin.serialization)
            }

            apply<KMPAndroidLibraryConvention>()
            applyCommonConventions()

            tasks.withType<Test> {
                useJUnitPlatform()
            }

            multiplatform {
                targets.all {}
                // TODO This line for some reason includes additional dependencies to various source sets???
                // Why tf does this include com.google.android.material:material
                // jvm {}

                with(sourceSets) {
                    commonMain.dependencies {
                        implementation(libs.androidx.annotation)
                    }

                    commonTest.dependencies {}

                    if (false) {
                        jvmMain.dependencies {
                            implementation(libs.kotlinx.coroutines.swing)
                        }

                        jvmTest.dependencies {
                            implementation(libs.kotlinx.coroutines.test)
                            implementation(libs.kotest.jUnit5runner)
                            implementation(project.dependencies.platform(libs.junit5.bom))
                            implementation(libs.junit5.jupiter.api)
                            implementation(libs.junit5.jupiter.runtime)
                        }
                    }
                }
            }
        },
    )
