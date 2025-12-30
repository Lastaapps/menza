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

package cz.lastaapps.plugin.android.config

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import cz.lastaapps.extensions.coreLibraryDesugaring
import cz.lastaapps.extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun printFields(instance: Any) {
    val kClass = instance::class

    // memberProperties returns KProperty1<T, *> instances
    kClass.memberProperties
        .map { prop ->
            // bypass JVM access checks for private members
            prop.isAccessible = true

            val value =
                try {
                    prop.call(instance)
                } catch (e: Exception) {
                    "Unreadable"
                }

            "${prop.name}: ${prop.returnType} = $value"
        }.joinToString(",\n") { it }
        .let { error(it) }
}

context(p: Project)
internal fun CommonExtension.configureAndroidOnlyModule() {
    compileSdk = p.getCompileSdk()

//    defaultConfig {
//        minSdk = getMinSdk()
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//    compileOptions {
//        val versionCode =
//            libs.versions.java.jvmTarget
//                .get()
//                .toInt()
//        val version = JavaVersion.toVersion(versionCode)
//        sourceCompatibility = version
//        targetCompatibility = version
//        isCoreLibraryDesugaringEnabled = true
//    }

//    dependencies {
//        coreLibraryDesugaring(libs.android.desugaring)
//    }
//    printFields(this)
}

context(p: Project)
internal fun KotlinMultiplatformAndroidLibraryExtension.configureAndroidKMPModule() {
    compileSdk = p.getCompileSdk()
    minSdk = p.getMinSdk()

    // TODO determine how does this behave in release/debug
    // optimization { minify = true }

    enableCoreLibraryDesugaring = true
    p.dependencies {
        coreLibraryDesugaring(p.libs.android.desugaring)
    }
}

private fun Project.getCompileSdk() =
    libs.versions.sdk.compile
        .get()
        .toInt()

private fun Project.getMinSdk() =
    libs.versions.sdk.min
        .get()
        .toInt()
