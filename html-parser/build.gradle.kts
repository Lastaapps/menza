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
    id(Plugins.KOTLIN_MULTIPLATFORM)
    id(Plugins.LIBRARY)
}

group = App.GROUP
version = App.VERSION_NAME

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin {
    sourceSets.all {
        languageSettings.apply {
            languageVersion = Versions.KOTLIN_LANGUAGE_VERSION
            apiVersion = Versions.KOTLIN_LANGUAGE_VERSION
        }
    }
    android {
        compilations.all {
            kotlinOptions.jvmTarget = Versions.JVM_TARGET
        }
    }
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = Versions.JVM_TARGET
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    val jsoup = "1.13.1" // 1.14.3
    sourceSets {
        val commonMain by getting {
            dependencies {
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            kotlin.srcDir("src/commonJvmAndroid/kotlin")
            dependencies {
                api("org.jsoup:jsoup:$jsoup")
            }
        }
        val androidTest by getting {
            kotlin.srcDir("src/commonJvmAndroidTest/kotlin")
            dependencies {
            }
        }
        val desktopMain by getting {
            kotlin.srcDir("src/commonJvmAndroid/kotlin")
            dependencies {
                api("org.jsoup:jsoup:$jsoup")
            }
        }
        val desktopTest by getting {
            kotlin.srcDir("src/commonJvmAndroidTest/kotlin")
            dependencies {
                implementation(Tests.JUNIT)
            }
        }
    }
}

android {
    compileSdk = App.COMPILE_SDK

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = App.MIN_SDK
        targetSdk = App.TARGET_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    namespace = "it.skrape"

    dependencies {
        coreLibraryDesugaring(Libs.DESUGARING)
    }
}

