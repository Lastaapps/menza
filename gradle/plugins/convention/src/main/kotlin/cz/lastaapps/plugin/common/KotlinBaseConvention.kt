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

import cz.lastaapps.plugin.BasePlugin
import cz.lastaapps.plugin.extensions.alias
import cz.lastaapps.plugin.extensions.compilerOptions
import cz.lastaapps.plugin.extensions.implementation
import cz.lastaapps.plugin.extensions.libs
import cz.lastaapps.plugin.extensions.multiplatform
import cz.lastaapps.plugin.extensions.pluginManager
import cz.lastaapps.plugin.extensions.testImplementation
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

private typealias KV = KotlinVersion

/**
 * Set up Kotlin Compiler and dependencies necessary for developer's sanity preservation
 */
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

                // Treat all Kotlin warnings as errors
                allWarningsAsErrors = true

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
                with(sourceSets) {
                    commonMain.dependencies {
                        dependenciesKotlinMain().forEach(::implementation)
                    }
                    commonTest.dependencies {
                        dependenciesKotlinTest().forEach(::implementation)
                    }
                }
            }
        },
        androidConfiguration = {
            dependencies {
                dependenciesKotlinMain().forEach(::implementation)
                dependenciesKotlinTest().forEach(::testImplementation)
            }
        },
    ) {
    companion object {
        private fun Project.dependenciesKotlinMain() =
            listOf(
                project.dependencies.platform(libs.kotlin.bom),
                libs.kotlinx.dateTime,
                libs.kotlinx.collection,
                // Essential libraries
                libs.kermit,
                libs.fluidLocale,
                // ArrowKt
                project.dependencies.platform(libs.arrowkt.bom),
                libs.arrowkt.core,
                libs.arrowkt.fx.coroutines,
                libs.arrowkt.fx.stm,
            )

        private fun Project.dependenciesKotlinTest() =
            listOf(
                // Kotlin test
                libs.kotlin.test.annotation,
                libs.kotlin.test.common,
                libs.kotlin.test.core,
                libs.kotlin.test.jUnit5,
                // Kotest
                libs.kotest.arrow,
                libs.kotest.assertion,
            )
    }
}
