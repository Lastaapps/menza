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

plugins {
    alias(libs.plugins.lastaapps.android.app)
    alias(libs.plugins.lastaapps.common.coil)
    alias(libs.plugins.lastaapps.common.compose)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.kotlin.atomicfu)
}

android {
    namespace = "cz.lastaapps.menza"

    compileSdk =
        libs.versions.sdk.compile
            .get()
            .toInt()

    defaultConfig {
        applicationId = "cz.lastaapps.menza"

        // have to be specified explicitly for FDroid to work
        versionCode = 1040000 // 1x major . 2x minor . 2x path . 2x build diff
        versionName = "1.4.0"

        minSdk =
            libs.versions.sdk.min
                .get()
                .toInt()
        targetSdk =
            libs.versions.sdk.target
                .get()
                .toInt()
    }

    packaging {
        // Remove some conflict between atomic-fu and datetime
        resources.pickFirsts.add("META-INF/versions/9/previous-compilation-data.bin")

        // Remove some crap skrape-it dependencies
        resources.pickFirsts.add("META-INF/DEPENDENCIES")
        resources.pickFirsts.add("mozilla/public-suffix-list.txt")

        // Remove sqldelight native sql driver for Windows and Mac
        resources.excludes.add("org/sqlite/native/**")
        // And some Apache shit
        resources.excludes.add("org/apache/**")
        resources.excludes.add("org/htmlunit/**")

        // Taken from the AS template project, should be safe
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
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

    implementation(libs.koin.android.startup)

    implementation(libs.aboutLibraries.core)

    implementation(libs.bundles.sqldelight)

    implementation(libs.kotlinx.serializationJson)
    implementation(libs.kotlinx.atomicfu)

    implementation(libs.bundles.russhwolf.settings)

    implementation(libs.ktor.client.core)
    // required by ktor internally (release only)
    // noinspection UseTomlInstead
    implementation("org.slf4j:slf4j-simple:2.0.16")
}
