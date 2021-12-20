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

object Libs {

    const val DESUGARING = "com.android.tools:desugar_jdk_libs:${Versions.DESUGAR}"

    const val KOTLIN_STANDART_LIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.KOTLIN}"
    const val KOTLIN_COROUTINES =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES}"
    const val KOTLIN_REFLECT = "org.jetbrains.kotlin:kotlin-reflect:${Versions.KOTLIN}"
    const val KTOR_CORE = "io.ktor:ktor-client-core:${Versions.KTOR}"
    const val KTOR_CIO = "io.ktor:ktor-client-cio:${Versions.KTOR}"
    const val KOTLINX_DATETIME =
        "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.KOTLINX_DATETIME}"

    const val ACTIVITY = "androidx.activity:activity-ktx:${Versions.ACTIVITY}"
    const val APPCOMPAT = "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"
    const val ANNOTATION = "androidx.annotation:annotation:${Versions.ANNOTATION}"
    const val COLLECTION = "androidx.collection:collection-ktx:${Versions.COLLECTION}"
    const val CONSTRAINTLAYOUT =
        "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINTLAYOUT}"
    const val CORE = "androidx.core:core-ktx:${Versions.CORE}"
    const val DATASTORE = "androidx.datastore:datastore-preferences:${Versions.DATASTORE}"
    const val DOCUMENT_FILE = "androidx.documentfile:documentfile:${Versions.DOCUMENT_FILE}"
    const val FRAGMENT = "androidx.fragment:fragment-ktx:${Versions.FRAGMENT}"
    const val LIFECYCLE = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE}"
    const val LIFECYCLE_LIVEDATA = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIFECYCLE}"
    const val LIFECYCLE_SERVICE = "androidx.lifecycle:lifecycle-service:${Versions.LIFECYCLE}"
    const val NAVIGATION = "androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION}"
    const val NAVIGATION_FRAGMENT =
        "androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION}"
    const val PREFERENCES = "androidx.preference:preference-ktx:${Versions.PREFERENCES}"
    const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:${Versions.RECYCLER_VIEW}"
    const val SPLASHSCREEN = "androidx.core:core-splashscreen:${Versions.SPLASHSCREEN}"
    const val STARTUP = "androidx.startup:startup-runtime:${Versions.STARTUP}"
    const val SWIPE_REFRESH_LAYOUT =
        "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.SWIPE_REFRESH_LAYOUT}"
    const val TRACING = "androidx.tracing:tracing-ktx:${Versions.TRACING}"
    const val VECTOR_DRAWABLES =
        "androidx.vectordrawable:vectordrawable:${Versions.VECTOR_DRAWABLES}"
    const val VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.LIFECYCLE}"
    const val WORK = "androidx.work:work-runtime-ktx:${Versions.WORK}"

    const val ROOM = "androidx.room:room-runtime:${Versions.ROOM}"
    const val ROOM_COMPILER = "androidx.room:room-compiler:${Versions.ROOM}"
    const val ROOM_KTX = "androidx.room:room-ktx:${Versions.ROOM}"

    const val MATERIAL = "com.google.android.material:material:${Versions.GOOGLE_MATERIAL}"
    const val OSS_LICENSE =
        "com.google.android.gms:play-services-oss-licenses:${Versions.OSS_LICENSE}"
    const val PLAY_SERVICES = "com.google.android.play:core-ktx:${Versions.PLAY_SERVICES}"

    const val FIREBASE_BOM = "com.google.firebase:firebase-bom:${Versions.FIREBASE_BOM}"
    const val FIREBASE_ANALYTICS = "com.google.firebase:firebase-analytics-ktx"
    const val FIREBASE_CONFIG = "com.google.firebase:firebase-config-ktx"
    const val FIREBASE_MESSAGING = "com.google.firebase:firebase-messaging-ktx"
    const val FIREBASE_CRASHLYTICS = "com.google.firebase:firebase-crashlytics-ktx"
    const val FIREBASE_DATABASE = "com.google.firebase:firebase-database-ktx"


    const val COMPOSE_ANIMATION = "androidx.compose.animation:animation:${Versions.COMPOSE}"
    const val COMPOSE_COMPILER = "androidx.compose.compiler:compiler:${Versions.COMPOSE}"
    const val COMPOSE_MATERIAL = "androidx.compose.material:material:${Versions.COMPOSE}"
    const val COMPOSE_ICONS_CORE =
        "androidx.compose.material:material-icons-core:${Versions.COMPOSE}"
    const val COMPOSE_ICONS_EXTENDED =
        "androidx.compose.material:material-icons-extended:${Versions.COMPOSE}"
    const val COMPOSE_FOUNDATION = "androidx.compose.foundation:foundation:${Versions.COMPOSE}"
    const val COMPOSE_UI = "androidx.compose.ui:ui:${Versions.COMPOSE}"
    const val COMPOSE_MATERIAL_3 =
        "androidx.compose.material3:material3:${Versions.COMPOSE_MATERIAL_3}"
    const val COMPOSE_TOOLING = "androidx.compose.ui:ui-tooling:${Versions.COMPOSE}"

    const val COMPOSE_ACTIVITY = "androidx.activity:activity-compose:${Versions.ACTIVITY}"
    const val COMPOSE_CONSTRAINTLAYOUT =
        "androidx.constraintlayout:constraintlayout-compose:${Versions.CONSTRAINTLAYOUT_COMPOSE}"
    const val COMPOSE_VIEWMODEL =
        "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.VIEWMODEL_COMPOSE}"
    const val COMPOSE_NAVIGATION = "androidx.navigation:navigation-compose:${Versions.NAVIGATION}"

    const val ACCOMPANIST_INSETS =
        "com.google.accompanist:accompanist-insets:${Versions.ACCOMPANIST}"
    const val ACCOMPANIST_SYSTEM_UI =
        "com.google.accompanist:accompanist-systemuicontroller:${Versions.ACCOMPANIST}"
    const val ACCOMPANIST_PAGER = "com.google.accompanist:accompanist-pager:${Versions.ACCOMPANIST}"
    const val ACCOMPANIST_PERMISSION =
        "com.google.accompanist:accompanist-permissions:${Versions.ACCOMPANIST}"
    const val ACCOMPANIST_PLACEHOLDER =
        "com.google.accompanist:accompanist-placeholder:${Versions.ACCOMPANIST}"
    const val ACCOMPANIST_FLOW_LAYOUTS =
        "com.google.accompanist:accompanist-flowlayout:${Versions.ACCOMPANIST}"
    const val ACCOMPANIST_NAVIGATION_ANIMATION =
        "com.google.accompanist:accompanist-navigation-animation:${Versions.ACCOMPANIST}"
    const val ACCOMPANIST_NAVIGATION_MATERIAL =
        "com.google.accompanist:accompanist-navigation-material:${Versions.ACCOMPANIST}"
    const val ACCOMPANIST_DRAWABLE_PAINTERS =
        "com.google.accompanist:accompanist-drawablepainter:${Versions.ACCOMPANIST}"
    const val ACCOMPANIST_SWIPE_TO_REFRESH =
        "com.google.accompanist:accompanist-swiperefresh:${Versions.ACCOMPANIST}"

}
