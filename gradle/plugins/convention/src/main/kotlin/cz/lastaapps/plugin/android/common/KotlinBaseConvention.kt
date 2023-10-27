/*
 *    Copyright 2023, Petr Laštovička as Lasta apps, All rights reserved
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
import cz.lastaapps.extensions.implementation
import cz.lastaapps.extensions.java
import cz.lastaapps.extensions.libs
import cz.lastaapps.extensions.pluginManager
import cz.lastaapps.plugin.BasePlugin
import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class KotlinBaseConvention : BasePlugin(
    {

        pluginManager {
            alias(libs.plugins.kotlin.serialization)
        }

        apply<CoroutinesConvention>()

        java {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        android { }

        tasks.withType<KotlinCompile> {
            kotlinOptions {
                languageVersion = libs.versions.kotlin.language.get()
                apiVersion = libs.versions.kotlin.api.get()

                freeCompilerArgs += listOf(
                    "-opt-in=kotlin.ExperimentalStdlibApi",
                    "-opt-in=kotlin.time.ExperimentalTime",
                    "-Xcontext-receivers",
                )
            }
        }

        dependencies {
            implementation(libs.kotlinx.dateTime)
            implementation(libs.kotlinx.collection)
            implementation(libs.kermit)
            implementation(libs.fluidLocale)
        }
    },
)
