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

package cz.lastaapps.plugin.multiplatform

import com.android.build.gradle.LibraryExtension
import cz.lastaapps.extensions.alias
import cz.lastaapps.extensions.commonImplementation
import cz.lastaapps.extensions.libs
import cz.lastaapps.extensions.multiplatform
import cz.lastaapps.extensions.pluginManager
import cz.lastaapps.plugin.BasePlugin
import cz.lastaapps.plugin.android.AndroidLibraryConvention
import cz.lastaapps.plugin.android.common.KotlinBaseConvention
import cz.lastaapps.plugin.android.config.configureKotlinAndroid
import cz.lastaapps.plugin.common.ArrowKtConvention
import cz.lastaapps.plugin.common.DetektConvention
import org.gradle.api.JavaVersion
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("unused")
class KMPLibraryConvention : BasePlugin({
    pluginManager {
        alias(libs.plugins.kotlin.multiplatform)
        alias(libs.plugins.kotlin.serialization)
        alias(libs.plugins.android.library)
    }

    apply<KotlinBaseConvention>()
    apply<AndroidLibraryConvention>()
    apply<DetektConvention>()
    apply<ArrowKtConvention>()

    afterEvaluate {
        multiplatform {
            sourceSets.removeAll { sourceSet ->
                setOf(
                    "androidAndroidTestRelease", "androidTestFixtures",
                    "androidTestFixturesDebug", "androidTestFixturesRelease",
                ).contains(sourceSet.name)
            }
        }
    }

    extensions.configure<LibraryExtension> {

        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

        configureKotlinAndroid(this)
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            languageVersion = libs.versions.kotlin.languageVersion.get()
            apiVersion = libs.versions.kotlin.languageVersion.get()
        }
    }

    multiplatform {

        sourceSets.all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                languageVersion = libs.versions.kotlin.languageVersion.get()
                apiVersion = libs.versions.kotlin.languageVersion.get()
            }
        }

        targets.all {
            compilations.all { }
        }

        android {
            compilations.all {
                kotlinOptions {
                    jvmTarget = JavaVersion.VERSION_11.toString()
                }
            }
        }
        jvm {
            compilations.all {
                kotlinOptions {
                    jvmTarget = JavaVersion.VERSION_11.toString()
                }
            }
        }

        sourceSets.apply {
            getByName("commonMain") {
                dependencies {
                    implementation(libs.kotlin.coroutines.common)
                    implementation(libs.kotlinx.dateTime)
                    implementation(libs.kotlinx.collection)
                    implementation(libs.kotlinx.serializationJson)
                    implementation(libs.kodein.core)
                    implementation(libs.koin.core)
//                    implementation(libs.koin.annotations)
                    implementation(libs.kmLogging)
                }
            }

            getByName("commonTest") {
                dependencies {
                    implementation(kotlin("test"))
                    implementation(libs.kotest.assertion)
                    implementation(libs.kotlin.coroutines.test)
                    implementation(libs.koin.test)
                }
            }

            getByName("androidMain") {
                dependencies {
                    // implementation(libs.koin.android.core)
                    implementation(libs.kodein.androidCore)
                    implementation(libs.kodein.androidx)
                    implementation(libs.kodein.androidxViewmode)
                    implementation(libs.kodein.androidxViewmodeSavedstate)
                }
            }

            getByName("androidTest") {
                dependencies {
                    implementation(libs.kotlin.coroutines.test)
                    implementation(libs.kotest.jUnit5runner)
                    implementation(project.dependencies.platform(libs.junit5.bom))
                    implementation(libs.junit5.jupiter.api)
                    implementation(libs.junit5.jupiter.runtime)
                }
            }

            getByName("jvmMain") {
                dependencies {
                    implementation(libs.logback.core)
                    implementation(libs.logback.classic)
                    implementation(libs.slf4j)
                }
            }

            getByName("jvmTest") {
                dependencies {
                    implementation(libs.kotlin.coroutines.test)
                    implementation(libs.kotest.jUnit5runner)
                    implementation(project.dependencies.platform(libs.junit5.bom))
                    implementation(libs.junit5.jupiter.api)
                    implementation(libs.junit5.jupiter.runtime)
                }
            }
        }
    }

    dependencies {
        try {
            add("kspCommonMainMetadata", libs.koin.annotations.compiler)
            add("kspAndroid", libs.koin.annotations.compiler)
            add("kspJvm", libs.koin.annotations.compiler)
        } catch (_: Exception) {
        }

        commonImplementation(platform(libs.arrowkt.bom))
        commonImplementation(libs.arrowkt.core)
        commonImplementation(libs.arrowkt.fx.coroutines)
        commonImplementation(libs.arrowkt.fx.stm)
    }
})
