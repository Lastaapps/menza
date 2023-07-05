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

plugins {
    alias(libs.plugins.lastaapps.android.app.compose)
    alias(libs.plugins.aboutLibraries)
}

android {
    namespace = "cz.lastaapps.menza"

    compileSdk = libs.versions.sdk.compile.get().toInt()

    defaultConfig {
        applicationId = "cz.lastaapps.menza"

        //have to be specified explicitly for FDroid to work
        versionCode = 1020700 // 1x major . 2x minor . 2x path . 2x build diff
        versionName = "1.2.7"

        minSdk = libs.versions.sdk.min.get().toInt()
        targetSdk = libs.versions.sdk.target.get().toInt()
    }

    configurations {
        all {
            //exclude(group = "org.apache.httpcomponents", module = "httpclient")
            exclude(group = "commons-logging", module = "commons-logging")
        }
    }
}

dependencies {

    // Just until they fix ViewModel stuff
    implementation("lib.stolen:appyx")

    implementation(projects.api.main)
    implementation(projects.core)
    implementation(projects.entity)
    implementation(projects.scraping)
    implementation(projects.storage.db)
    implementation(projects.storage.repo)
    implementation(projects.lastaapps.common)
    implementation(projects.lastaapps.crash)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.emoji2.bundled)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.vectorDrawables)
    implementation(libs.androidx.windowManager)
    implementation(libs.google.material)

    implementation(libs.aboutLibraries.core)

    implementation(libs.bundles.sqldelight)

    implementation(libs.kotlinx.serializationJson)

    implementation(libs.bundles.russhwolf.settings)
}
