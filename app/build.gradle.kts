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

plugins {
    alias(libs.plugins.lastaapps.android.app.compose)
    alias(libs.plugins.lastaapps.common.coil)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.kotlin.atomicfu)
}

android {
    namespace = "cz.lastaapps.menza"

    compileSdk = libs.versions.sdk.compile.get().toInt()

    defaultConfig {
        applicationId = "cz.lastaapps.menza"

        // have to be specified explicitly for FDroid to work
        versionCode = 1020700 // 1x major . 2x minor . 2x path . 2x build diff
        versionName = "1.2.7"

        minSdk = libs.versions.sdk.min.get().toInt()
        targetSdk = libs.versions.sdk.target.get().toInt()
    }

    configurations {
        all {
            // exclude(group = "org.apache.httpcomponents", module = "httpclient")
            exclude(group = "commons-logging", module = "commons-logging")
        }
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

            val debug = getByName("debug")
            signingConfig = debug.signingConfig
            isDebuggable = true
        }

        /* Used for release testing without explicit signing.
         * This is required to make sure that release variants of the libraries are used,
         * as they can differ (Compose, Lifecycle, ...)
         * You can set if minification is on (useful for debugging)
         */
        getByName("fakeRelease") {
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
                    arrayOf(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro",
                    )
                } else {
                    emptyArray()
                }
            }.let { rules ->
                proguardFiles(*rules)
            }
        }
    }

    packaging {
        // Remove some conflict between atomic-fu and datetime
        resources.pickFirsts.add("META-INF/versions/9/previous-compilation-data.bin")

        // Remove some crap skrape-it dependencies
        resources.pickFirsts.add("META-INF/DEPENDENCIES")
        resources.pickFirsts.add("mozilla/public-suffix-list.txt")

        // Remove sqldelight native sql driver for Widnows and Mac
        resources.excludes.add("org/sqlite/native/**")
    }
}

dependencies {

    implementation(projects.api.agata)
    implementation(projects.api.main)
    implementation(projects.core)
    implementation(projects.lastaapps.common)
    implementation(projects.lastaapps.crash)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.emoji2.core)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.vectorDrawables)
    implementation(libs.androidx.windowManager)
    implementation(libs.google.material)

    implementation(libs.aboutLibraries.core)

    implementation(libs.bundles.sqldelight)

    implementation(libs.kotlinx.serializationJson)
    implementation(libs.kotlinx.atomicfu)

    implementation(libs.bundles.russhwolf.settings)

    implementation(libs.ktor.client.core)
    // required by ktor internally (release only)
    //noinspection UseTomlInstead
    implementation("org.slf4j:slf4j-simple:2.0.6")
}
