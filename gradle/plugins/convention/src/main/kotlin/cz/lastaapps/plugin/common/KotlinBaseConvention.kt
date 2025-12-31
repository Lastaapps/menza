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

import cz.lastaapps.extensions.alias
import cz.lastaapps.extensions.compilerOptions
import cz.lastaapps.extensions.implementation
import cz.lastaapps.extensions.libs
import cz.lastaapps.extensions.multiplatform
import cz.lastaapps.extensions.pluginManager
import cz.lastaapps.plugin.BasePlugin
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

private typealias KV = KotlinVersion

class KotlinBaseConvention :
    BasePlugin(
        configuration = {
            pluginManager {
                alias(libs.plugins.kotlin.serialization)
            }

            kotlinExtension.sourceSets.all {
                languageSettings.optIn("kotlin.time.ExperimentalTime")
            }

            compilerOptions(
                {
                    val versionCode =
                        libs.versions.java.jvmTarget
                            .get()
                            .toInt()
                    val version = JavaVersion.toVersion(versionCode)
                    jvmTarget.set(JvmTarget.fromTarget(version.toString()))
                },
            ) {
                languageVersion.set(
                    KV.fromVersion(
                        libs.versions.kotlin.language
                            .get(),
                    ),
                )
                apiVersion.set(
                    KV.fromVersion(
                        libs.versions.kotlin.api
                            .get(),
                    ),
                )

                // Treat all Kotlin warnings as errors (disabled by default)
                allWarningsAsErrors = properties["warningsAsErrors"] as? Boolean ?: false

                freeCompilerArgs.addAll(
                    listOf(
                        "-opt-in=kotlin.ExperimentalStdlibApi",
                        "-opt-in=kotlin.RequiresOptIn",
                        "-Xannotation-default-target=param-property",
                        "-Xannotation-target-all",
                        "-Xcontext-parameters",
                        "-Xcontext-sensitive-resolution",
                        // enforce Java nullability
                        "-Xjspecify-annotations=strict",
                        "-Xnested-type-aliases",
                        "-Xtype-enhancement-improvements-strict-mode",
                        "-Xwhen-guards",
                    ),
                )
            }
        },
        kmpConfiguration = {
            multiplatform {
                sourceSets.commonMain.dependencies {
                    dependenciesKotlinBase().forEach(::implementation)
                    dependenciesGeneral().forEach(::implementation)
                    dependenciesArrowKt().forEach(::implementation)
                }
            }
        },
        androidConfiguration = {
            dependencies {
                dependenciesKotlinBase().forEach(::implementation)
                dependenciesGeneral().forEach(::implementation)
                dependenciesArrowKt().forEach(::implementation)
            }
        },
    ) {
    companion object {
        private fun Project.dependenciesKotlinBase() =
            listOf(
                project.dependencies.platform(libs.kotlin.bom),
                libs.kotlinx.dateTime,
                libs.kotlinx.collection,
            )

        private fun Project.dependenciesGeneral() =
            listOf(
                libs.kermit,
                libs.fluidLocale,
            )

        fun Project.dependenciesArrowKt() =
            listOf(
                (project.dependencies.platform(libs.arrowkt.bom)),
                (libs.arrowkt.core),
                (libs.arrowkt.fx.coroutines),
                (libs.arrowkt.fx.stm),
            )
    }
}
