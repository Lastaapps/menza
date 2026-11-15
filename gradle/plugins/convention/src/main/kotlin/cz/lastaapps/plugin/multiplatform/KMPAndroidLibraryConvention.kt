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

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.android.build.api.variant.KotlinMultiplatformAndroidComponentsExtension
import cz.lastaapps.plugin.BasePlugin
import cz.lastaapps.plugin.android.config.configureAndroidKMPModule
import cz.lastaapps.plugin.extensions.alias
import cz.lastaapps.plugin.extensions.libs
import cz.lastaapps.plugin.extensions.multiplatform
import cz.lastaapps.plugin.extensions.pluginManager
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure

class KMPAndroidLibraryConvention :
    BasePlugin(
        {
            pluginManager {
                alias(libs.plugins.android.library.kmp)
            }

            extensions.configure<KotlinMultiplatformAndroidComponentsExtension> {
                @Suppress("unused")
                onVariants { variant -> // e.g. variant.name = "androidMain"
                }
            }

            multiplatform {
                (
                    (this as ExtensionAware).extensions.findByType(
                        KotlinMultiplatformAndroidLibraryTarget::class.java,
                    ) ?: error("KMP Android lib plugin not applied")
                ).apply {
                    configureAndroidKMPModule()
                    // withHostTestBuilder {}.configure {}
                }

                // in case tests are enabled, add required dependencies
                sourceSets.findByName("androidHostTest")?.dependencies {
                    implementation(libs.kotest.jUnit5runner)
                    implementation(project.dependencies.platform(libs.junit5.bom))
                    implementation(libs.junit5.jupiter.api)
                    implementation(libs.junit5.jupiter.runtime)
                }
                sourceSets.findByName("androidDeviceTest")?.dependencies {}
            }
        },
    )
