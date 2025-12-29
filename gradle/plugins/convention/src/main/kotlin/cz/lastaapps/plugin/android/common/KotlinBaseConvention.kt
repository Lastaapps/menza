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
import cz.lastaapps.extensions.compilerOptions
import cz.lastaapps.extensions.java
import cz.lastaapps.extensions.libs
import cz.lastaapps.extensions.pluginManager
import cz.lastaapps.plugin.BasePlugin
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

private typealias KV = org.jetbrains.kotlin.gradle.dsl.KotlinVersion

class KotlinBaseConvention :
    BasePlugin(
        {
            pluginManager {
                alias(libs.plugins.kotlin.serialization)
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
    ) {
    companion object {
        context(p: Project)
        fun KotlinDependencyHandler.dependenciesKotlinBase() {
            implementation(p.project.dependencies.platform(p.libs.kotlin.bom))
            implementation(p.libs.kotlinx.dateTime)
            implementation(p.libs.kotlinx.collection)
            implementation(p.libs.kermit)
            implementation(p.libs.fluidLocale)
        }
    }
}
