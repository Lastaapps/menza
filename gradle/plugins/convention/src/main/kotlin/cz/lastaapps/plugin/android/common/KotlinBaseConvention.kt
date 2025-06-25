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

import cz.lastaapps.extensions.alias
import cz.lastaapps.extensions.android
import cz.lastaapps.extensions.compilerOptions
import cz.lastaapps.extensions.implementation
import cz.lastaapps.extensions.java
import cz.lastaapps.extensions.libs
import cz.lastaapps.extensions.pluginManager
import cz.lastaapps.plugin.BasePlugin
import org.gradle.api.JavaVersion
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

private typealias KV = org.jetbrains.kotlin.gradle.dsl.KotlinVersion

class KotlinBaseConvention :
    BasePlugin(
        {
            pluginManager {
                alias(libs.plugins.kotlin.serialization)
            }

            apply<CoroutinesConvention>()

            kotlinExtension.sourceSets.all {
                languageSettings.optIn("kotlin.time.ExperimentalTime")
            }

            java {
                val versionCode =
                    libs.versions.java.jvmTarget
                        .get()
                        .toInt()
                val version = JavaVersion.toVersion(versionCode)
                sourceCompatibility = version
                targetCompatibility = version
                toolchain {
                    languageVersion.set(JavaLanguageVersion.of(versionCode))
                }
            }

            android { }

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

                freeCompilerArgs.addAll(
                    listOf(
                        "-opt-in=kotlin.ExperimentalStdlibApi",
                        "-Xwhen-guards",
                        "-Xcontext-parameters",
                        "-Xcontext-sensitive-resolution",
                        "-Xannotation-target-all",
                        "-Xnested-type-aliases",
                    ),
                )
            }

            dependencies {
                implementation(libs.kotlinx.dateTime)
                implementation(libs.kotlinx.collection)
                implementation(libs.kermit)
                implementation(libs.fluidLocale)
            }
        },
    )
