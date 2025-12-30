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

import cz.lastaapps.extensions.alias
import cz.lastaapps.extensions.libs
import cz.lastaapps.extensions.multiplatform
import cz.lastaapps.extensions.pluginManager
import cz.lastaapps.plugin.BasePlugin
import cz.lastaapps.plugin.android.common.CoroutinesConvention
import cz.lastaapps.plugin.android.common.KotlinBaseConvention
import cz.lastaapps.plugin.android.common.KotlinBaseConvention.Companion.dependenciesKotlinBase
import cz.lastaapps.plugin.common.ComposeRuntimeConvention
import cz.lastaapps.plugin.common.DetektConvention
import cz.lastaapps.plugin.common.JavaToolchainConvention
import cz.lastaapps.plugin.common.KMPAndroidLibraryConvention
import cz.lastaapps.plugin.common.KoinConvention
import cz.lastaapps.plugin.common.KtLintConvention
import cz.lastaapps.plugin.dependenciesArrowKt
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
            apply<ComposeRuntimeConvention>()
            apply<CoroutinesConvention>()
            apply<DetektConvention>()
            apply<JavaToolchainConvention>()
            apply<KotlinBaseConvention>()
            apply<KtLintConvention>()
            apply<KoinConvention>()

            tasks.withType<Test> {
                useJUnitPlatform()
            }

            multiplatform {
                targets.all {}
                // This line for some reason includes additional dependencies to various source sets???
                // Why tf does this include com.google.android.material:material
                // jvm {}

                sourceSets.apply {
                    commonMain.dependencies {
                        dependenciesKotlinBase().forEach(::implementation)
                        dependenciesArrowKt().forEach(::implementation)

                        implementation(libs.koin.core)
//                    implementation(libs.koin.annotations)
                        implementation(libs.kermit)
                        implementation(libs.androidx.annotation)
                    }

                    commonTest.dependencies {
//                        implementation(libs.kotlin.test.annotation)
//                        implementation(libs.kotlin.test.common)
//                        implementation(libs.kotlin.test.core)
//                        implementation(libs.kotlin.test.jUnit5)
                        implementation(libs.kotest.arrow)
                        implementation(libs.kotest.assertion)
//                    implementation(libs.koin.test.jUnit5)
                    }

//                    getByName("androidUnitTest") {
//                        dependencies {
//                            implementation(libs.kotlinx.coroutines.test)
//                            implementation(libs.kotest.jUnit5runner)
//                            implementation(project.dependencies.platform(libs.junit5.bom))
//                            implementation(libs.junit5.jupiter.api)
//                            implementation(libs.junit5.jupiter.runtime)
//                        }
//                    }

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
