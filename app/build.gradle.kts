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
    id(Plugins.APPLICATION)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.KAPT)
    id(Plugins.DAGGER_HILT)
    id(Plugins.OSS_LICENSE)
}

project.group = App.GROUP

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
    packagingOptions {
        resources.excludes.add("META-INF/*")
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = Versions.JAVA
        targetCompatibility = Versions.JAVA
    }

    kotlinOptions {
        jvmTarget = Versions.JVM_TARGET
        freeCompilerArgs = listOf("-Xjvm-default=compatibility")
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.COMPOSE_COMPILER
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    coreLibraryDesugaring(Libs.DESUGARING)

    implementation(project(":entity"))
    implementation(project(":scraping"))
    implementation(project(":storage:db"))
    implementation(project(":storage:repo"))
    implementation(project(":lastaapps:common"))

    implementation(Libs.SPLASHSCREEN)
    implementation(Libs.MATERIAL)
    implementation(Libs.CORE)
    implementation(Libs.LIFECYCLE)
    implementation(Libs.WINDOW_MANAGER)
    implementation(Libs.VECTOR_DRAWABLES)

    implementation(Libs.DAGGER_HILT)
    implementation(Libs.HILT_COMMON)
    implementation(Libs.DATASTORE)
    implementation(Libs.HILT_VIEWMODEL)
    implementation(Libs.HILT_NAVIGATION_COMPOSE)
    kapt(Libs.DAGGER_HILT_COMPILER)
    kapt(Libs.HILT_COMPILER)

    initCompose(useMaterial2 = true)

    implementation(Libs.KOTLINX_DATETIME)
    implementation(Libs.SKRAPE_IT)
    implementation(Libs.COIL_COMPOSE_COMPLETE)
    implementation(Libs.OSS_LICENSE_ACCESSOR)

}

fun DependencyHandler.initCompose(useMaterial2: Boolean = false, useMaterial3: Boolean = true) {
    implementation(Libs.COMPOSE_ACTIVITY)
    implementation(Libs.COMPOSE_ANIMATION)
    implementation(Libs.COMPOSE_CONSTRAINTLAYOUT)
    implementation(Libs.COMPOSE_FOUNDATION)
    implementation(Libs.COMPOSE_ICONS_EXTENDED)
    if (useMaterial2)
        implementation(Libs.COMPOSE_MATERIAL)
    if (useMaterial3)
        implementation(Libs.COMPOSE_MATERIAL_3)
    implementation(Libs.COMPOSE_NAVIGATION)
    implementation(Libs.COMPOSE_TOOLING)
    implementation(Libs.COMPOSE_UI)
    implementation(Libs.COMPOSE_VIEWMODEL)

    implementation(Libs.ACCOMPANIST_DRAWABLE_PAINTERS)
    implementation(Libs.ACCOMPANIST_FLOW_LAYOUTS)
    implementation(Libs.ACCOMPANIST_INSETS)
    implementation(Libs.ACCOMPANIST_NAVIGATION_ANIMATION)
    implementation(Libs.ACCOMPANIST_NAVIGATION_MATERIAL)
    implementation(Libs.ACCOMPANIST_PAGER)
    implementation(Libs.ACCOMPANIST_PERMISSION)
    implementation(Libs.ACCOMPANIST_PLACEHOLDER)
    implementation(Libs.ACCOMPANIST_SWIPE_TO_REFRESH)
    implementation(Libs.ACCOMPANIST_SYSTEM_UI)
}
