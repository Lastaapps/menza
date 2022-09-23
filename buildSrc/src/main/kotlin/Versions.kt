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

import org.gradle.api.JavaVersion

object Versions {

    val JAVA = JavaVersion.VERSION_11
    const val JVM_TARGET = "11"

    //Versions
    //Studio
    const val DESUGAR = "1.2.2"

    //JetBrains
    const val KOTLIN = "1.7.10"
    const val KOTLIN_LANGUAGE_VERSION = "1.7"
    const val KSP = "$KOTLIN-1.0.6"
    const val COROUTINES = "1.6.4"
    const val SERIALIZATION = "1.4.0"
    const val KTOR = "2.1.1"

    //androidx
    const val ACTIVITY = "1.6.0"
    const val ANNOTATION = "1.4.0"
    const val APPCOMPAT = "1.6.0-rc01"
    const val APP_SEARCH = "1.1.0-alpha01"
    const val CAMERAX = "1.2.0-alpha02"
    const val COLLECTION = "1.2.0"
    const val CONSTRAINTLAYOUT = "2.1.3"
    const val CORE = "1.9.0"
    const val DAGGER_HILT = "2.44"
    const val DATASTORE = "1.0.0"
    const val DOCUMENT_FILE = "1.1.0-alpha01"
    const val EMOJI = "1.2.0-alpha04"
    const val FRAGMENT = "1.5.0-rc01"
    const val HILT = "1.0.0"
    const val LIFECYCLE = "2.6.0-alpha02"
    const val NAVIGATION = "2.6.0-alpha01"
    const val PREFERENCES = "1.2.0-rc01"
    const val RECYCLER_VIEW = "1.3.0-alpha02"
    const val ROOM = "2.4.2"
    const val SPLASHSCREEN = "1.0.0"
    const val STARTUP = "1.1.1"
    const val SWIPE_REFRESH_LAYOUT = "1.2.0-alpha01"
    const val TRACING = "1.1.0"
    const val VECTOR_DRAWABLES = "1.2.0-beta01"
    const val WINDOW_MANAGER = "1.1.0-alpha03"
    const val WORK = "2.8.0-alpha02"


    //compose
    const val COMPOSE = "1.3.0-beta03"
    const val COMPOSE_COMPILER = "1.3.1"
    const val COMPOSE_MATERIAL_3 = "1.0.0-beta02"
    const val CONSTRAINTLAYOUT_COMPOSE = "1.0.1"
    const val VIEWMODEL_COMPOSE = LIFECYCLE
    const val HILT_NAVIGATION_COMPOSE = "1.0.0"

    //google
    const val GOOGLE_MATERIAL = "1.6.1"
    const val PLAY_SERVICES = "1.8.1"
    const val PLAY_SERVICES_LOCATION = "20.0.0"
    const val MLKIT_BARCODE = "17.0.2"
    const val ACCOMPANIST = "0.26.4-beta"

    //firebase
    const val FIREBASE_BOM = "31.1.0"

    //others
    const val ABOUT_LIBRARIES = "10.4.0"
    const val COIL = "2.2.1"
    const val KM_LOGGING = "1.1.1"
    const val KODEIN = "7.14.0"
    const val KOTEST = "5.4.2"
    const val KOTLINX_DATETIME = "0.4.0"
    const val KOTLINX_COLLECTION = "0.3.5"
    const val LOGBACK = "1.2.11"
    const val QRGEN = "2.6.0"
    const val SKRAPE_IT = "1.2.1"
    const val SQLDELIGHT = "1.5.3"

    //testing android
    const val TEST_JUNIT = "4.13.2"
    const val TEST_ARCH = "2.1.0"
    const val TEST_ANDROIDX = "1.4.0"
    const val TEST_ANDROIDX_JUNIT = "1.1.3"
    const val TEST_KOTLIN_COROUTINES = COROUTINES
    const val TEST_ROBOELECTRIC = "4.6.1"
    const val TEST_GOOGLE_TRUTH = "1.1.3"
    const val TEST_ESPRESSO_CORE = "3.4.0"

}
