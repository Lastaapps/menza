/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
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

import cz.lastaapps.extensions.*
import cz.lastaapps.plugin.BasePlugin
import cz.lastaapps.plugin.common.DetektConvention
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("unused")
class JvmAppConvention : BasePlugin({
    pluginManager {
        apply("org.gradle.application")
        alias(libs.plugins.kotlin.jvm)
        alias(libs.plugins.shadow)
    }

    apply<DetektConvention>()

    tasks.withType<Test> {
        useJUnitPlatform()
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            languageVersion = libs.versions.kotlin.languageVersion.get()
            apiVersion = libs.versions.kotlin.languageVersion.get()
        }
    }

    (kotlinExtension as KotlinJvmProjectExtension).apply {
        sourceSets.all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                languageVersion = libs.versions.kotlin.languageVersion.get()
                apiVersion = libs.versions.kotlin.languageVersion.get()
            }
        }
    }

    dependencies {
        implementation(libs.kotlin.coroutines.common)
        implementation(libs.kotlinx.dateTime)
        implementation(libs.kotlinx.collection)
        implementation(libs.koin.core)
        implementation(libs.kmLogging)
        implementation(libs.fluidLocale)

        implementation(libs.logback.core)
        implementation(libs.logback.classic)
        implementation(libs.slf4j)

        testImplementation(kotlin("test"))
        testImplementation(libs.kotest.assertion)
        testImplementation(libs.kotlin.coroutines.test)
        testImplementation(libs.kotest.jUnit5runner)
        testImplementation(project.dependencies.platform(libs.junit5.bom))
        testImplementation(libs.junit5.jupiter.api)
        testImplementation(libs.junit5.jupiter.runtime)
    }
})