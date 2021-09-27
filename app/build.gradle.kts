/*
 *    Copyright 2021, Petr Laštovička as Lasta apps, All rights reserved
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
    id(Plugins.APPLICATION)
    id(Plugins.KOTLIN)
}

android {

    if (App.USE_LEGACY) {
        compileSdk = App.COMPILE_SDK
        buildToolsVersion = App.BUILD_TOOLS

        defaultConfig.targetSdk = App.TARGET_SDK
    } else {
        compileSdk = App.LEGACY_COMPILE_SDK
        buildToolsVersion = App.LEGACY_BUILD_TOOLS

        defaultConfig.targetSdk = App.LEGACY_TARGET_SDK
    }

    defaultConfig {
        applicationId = App.APP_ID
        versionCode = App.VERSION_CODE
        versionName = App.VERSION_NAME

        minSdk = App.MIN_SDK

        resourceConfigurations.addAll(setOf("en", "cs"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

        multiDexEnabled = true

    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"

            extra.set("alwaysUpdateBuildId", false)

            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    buildFeatures {
        buildConfig = false
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = Versions.JAVA
        targetCompatibility = Versions.JAVA
    }

    kotlinOptions {
        jvmTarget = Versions.JVM_TARGET
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    coreLibraryDesugaring(Libs.DESUGARING)

    implementation(Libs.CORE)
    implementation(Libs.LIFECYCLE)
    implementation(Libs.COMPOSE_ACTIVITY)

    implementation(Libs.COMPOSE_UI)
    implementation(Libs.COMPOSE_MATERIAL)
    implementation(Libs.COMPOSE_TOOLING)

}