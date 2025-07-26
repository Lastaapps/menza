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

package cz.lastaapps.plugin.android

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import cz.lastaapps.extensions.alias
import cz.lastaapps.extensions.implementation
import cz.lastaapps.extensions.libs
import cz.lastaapps.extensions.pluginManager
import cz.lastaapps.plugin.BasePlugin
import cz.lastaapps.plugin.android.config.configureKotlinAndroid
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.extra

class AndroidAppConvention :
    BasePlugin(
        {
            pluginManager {
                alias(libs.plugins.android.application)
                alias(libs.plugins.kotlin.android)
            }

            extensions.configure<BaseAppModuleExtension> {
                configureKotlinAndroid(this)

                defaultConfig {
                    targetSdk =
                        libs.versions.sdk.target
                            .get()
                            .toInt()
                    multiDexEnabled = true
                }

                androidResources.localeFilters += setOf("en", "cs")

                buildFeatures {
                    buildConfig = true
                }

                buildTypes {
                    debug {
                        applicationIdSuffix = ".debug"
                        isMinifyEnabled = false

                        extra.set("alwaysUpdateBuildId", false)
                    }
                    release {
                        isMinifyEnabled = true
                        isShrinkResources = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro",
                        )
                    }

                /* Used for release testing without explicit signing.
                 * This is required to make sure that release variants of the libraries are used,
                 * as they can differ (Compose, Lifecycle, ...)
                 * You can set if minification is on (useful for debugging)
                 */
                    create("fakeRelease") {
                        initWith(getByName("release"))
                        matchingFallbacks += listOf("release")

                        val debug = getByName("debug")
                        applicationIdSuffix = debug.applicationIdSuffix
                        signingConfig = debug.signingConfig
                        isDebuggable = true

                        val minify = true
                        isMinifyEnabled = minify
                        isShrinkResources = minify

                        run {
                            if (minify) {
                                arrayOf<Any>(
                                    getDefaultProguardFile("proguard-android-optimize.txt"),
                                    "proguard-rules.pro",
                                )
                            } else {
                                emptyArray<Any>()
                            }
                        }.let { rules ->
                            proguardFiles(*rules)
                        }
                    }
                }

                packaging {
                    resources {
                        // TODO Remove after skrape-it is removed
                        excludes.add("META-INF/INDEX.LIST")
                    }
                }
            }

            apply<AndroidBaseConvention>()

            dependencies {
                implementation(libs.google.material)
                implementation(libs.androidx.splashscreen)
                implementation(libs.androidx.startup)
                implementation(libs.androidx.vectorDrawables)
            }
        },
    )
