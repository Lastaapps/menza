/*
 *    Copyright 2024, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.plugin.jvm

import cz.lastaapps.extensions.alias
import cz.lastaapps.extensions.implementation
import cz.lastaapps.extensions.libs
import cz.lastaapps.extensions.pluginManager
import cz.lastaapps.extensions.testImplementation
import cz.lastaapps.plugin.BasePlugin
import cz.lastaapps.plugin.common.ArrowKtConvention
import cz.lastaapps.plugin.common.DetektConvention
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("unused")
class JvmAppConvention : BasePlugin(
    {
        pluginManager {
            apply("org.gradle.application")
            alias(libs.plugins.kotlin.jvm)
            alias(libs.plugins.shadow)
        }

        apply<DetektConvention>()
        apply<ArrowKtConvention>()

        tasks.withType<Test> {
            useJUnitPlatform()
        }
        tasks.withType<KotlinCompile> {
            kotlinOptions {
                languageVersion = libs.versions.kotlin.language.get()
                apiVersion = libs.versions.kotlin.api.get()
            }
        }

        (kotlinExtension as KotlinJvmProjectExtension).apply {
            sourceSets.all {
                languageSettings.apply {
                    optIn("kotlin.RequiresOptIn")
                    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                }
            }
        }

        dependencies {
            implementation(libs.kotlinx.coroutines.common)
            implementation(libs.kotlinx.dateTime)
            implementation(libs.kotlinx.collection)
            implementation(libs.koin.core)
            implementation(libs.kermit)
            implementation(libs.fluidLocale)

            testImplementation(libs.kotlin.test.annotation)
            testImplementation(libs.kotlin.test.common)
            testImplementation(libs.kotlin.test.core)
            testImplementation(libs.kotest.assertion)
            testImplementation(libs.kotlinx.coroutines.test)
            testImplementation(libs.kotest.jUnit5runner)
            testImplementation(project.dependencies.platform(libs.junit5.bom))
            testImplementation(libs.junit5.jupiter.api)
            testImplementation(libs.junit5.jupiter.runtime)
        }
    },
)