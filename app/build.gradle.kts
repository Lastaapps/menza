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
    id(Plugins.ABOUT_LIBRARIES)
}

project.group = App.GROUP

android {

    if (App.USE_PREVIEW) {
        compileSdk = App.PREVIEW_COMPILE_SDK
        defaultConfig.targetSdk = App.PREVIEW_TARGET_SDK
    } else {
        compileSdk = App.COMPILE_SDK
        defaultConfig.targetSdk = App.TARGET_SDK
    }

    defaultConfig {
        applicationId = App.APP_ID

        //have to be specified explicitly for FDroid to work
        versionCode = 1020000 // 1x major . 2x minor . 2x path . 2x build diff
        versionName = "1.2.0"
        require(versionCode == App.VERSION_CODE)
        require(versionName == App.VERSION_NAME)

        minSdk = App.MIN_SDK

        resourceConfigurations.addAll(setOf("en", "cs"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        multiDexEnabled = true

    }
    configurations {
        all {
            //exclude(group = "org.apache.httpcomponents", module = "httpclient")
            exclude(group = "commons-logging", module = "commons-logging")
        }
    }
    lint {
        abortOnError = false
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
    packagingOptions {
        resources.excludes.add("META-INF/*")
        resources.excludes.add("mozilla/*")
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = Versions.JAVA
        targetCompatibility = Versions.JAVA
    }
    kotlinOptions {
        jvmTarget = Versions.JVM_TARGET
        freeCompilerArgs = listOf(
            "-Xjvm-default=all-compatibility",
            "-opt-in=kotlin.RequiresOptIn",
            "-Xbackend-threads=4",
        )
        languageVersion = Versions.KOTLIN_LANGUAGE_VERSION
        apiVersion = Versions.KOTLIN_LANGUAGE_VERSION
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
    implementation(project(":lastaapps:crash"))

    implementation(Libs.SPLASHSCREEN)
    implementation(Libs.MATERIAL)
    implementation(Libs.CORE)
    implementation(Libs.DATASTORE)
    implementation(Libs.LIFECYCLE)
    implementation(Libs.STARTUP)
    implementation(Libs.WINDOW_MANAGER)
    implementation(Libs.VECTOR_DRAWABLES)

    implementation(Libs.DAGGER_HILT)
    implementation(Libs.HILT_COMMON)
    implementation(Libs.HILT_NAVIGATION_COMPOSE)
    kapt(Libs.DAGGER_HILT_COMPILER)
    kapt(Libs.HILT_COMPILER)

    initCompose()

    implementation(Libs.KOTLINX_DATETIME)
    implementation(Libs.COIL_COMPOSE_COMPLETE)
    implementation(Libs.ABOUT_LIBRARIES_CORE)

}

fun DependencyHandler.initCompose() {
    implementation(Libs.COMPOSE_ACTIVITY)
    implementation(Libs.COMPOSE_ANIMATION)
    implementation(Libs.COMPOSE_CONSTRAINTLAYOUT)
    implementation(Libs.COMPOSE_FOUNDATION)
    implementation(Libs.COMPOSE_ICONS_EXTENDED)
    implementation(Libs.COMPOSE_MATERIAL_3)
    implementation(Libs.COMPOSE_NAVIGATION)
    implementation(Libs.COMPOSE_TOOLING)
    implementation(Libs.COMPOSE_UI)
    implementation(Libs.COMPOSE_VIEWMODEL)

    implementation(Libs.ACCOMPANIST_DRAWABLE_PAINTERS)
    implementation(Libs.ACCOMPANIST_FLOW_LAYOUTS)
    implementation(Libs.ACCOMPANIST_NAVIGATION_ANIMATION)
    implementation(Libs.ACCOMPANIST_NAVIGATION_MATERIAL)
    implementation(Libs.ACCOMPANIST_PAGER)
    implementation(Libs.ACCOMPANIST_PERMISSION)
    implementation(Libs.ACCOMPANIST_PLACEHOLDER)
    implementation(Libs.ACCOMPANIST_SWIPE_TO_REFRESH)
    implementation(Libs.ACCOMPANIST_SYSTEM_UI)
}
