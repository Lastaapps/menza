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

plugins {
    id(Plugins.LIBRARY)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.KAPT)
    id(Plugins.DAGGER_HILT)
    id(Plugins.SQLDELIGHT) version Versions.SQLDELIGHT
}

android {
    compileSdk = App.COMPILE_SDK

    defaultConfig {
        minSdk = App.MIN_SDK
        targetSdk = App.TARGET_SDK

        multiDexEnabled = true
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = Versions.JAVA
        targetCompatibility = Versions.JAVA
    }
    kotlinOptions {
        jvmTarget = Versions.JVM_TARGET
    }
}

sqldelight {
    database("CrashDatabase") {
        packageName = "cz.lastaapps.crash"
        sourceFolders = listOf("sqldelight")
    }
}

dependencies {
    coreLibraryDesugaring(Libs.DESUGARING)

    implementation(Libs.STARTUP)

    implementation(Libs.SQLDELIGHT_ANDROID)
    implementation(Libs.SQLDELIGHT_RUNTIME)
    implementation(Libs.SQLDELIGHT_COROUTINES)

    implementation(Libs.DAGGER_HILT)
    implementation(Libs.HILT_COMMON)
    kapt(Libs.DAGGER_HILT_COMPILER)
    kapt(Libs.HILT_COMPILER)

    implementation(Libs.KM_LOGGING)
}