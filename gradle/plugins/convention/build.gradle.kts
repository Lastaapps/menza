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
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "cz.lastaapps.convention-plugins"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    kotlinDslPluginOptions {
        jvmTarget.set(JavaVersion.VERSION_11.toString())
    }
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    compileOnly(libs.gradlePlugins.kotlin)
    compileOnly(libs.gradlePlugins.android)
    implementation(libs.gradlePlugins.detekt)
}

gradlePlugin {
    plugins {
        val ids = libs.plugins.lastaapps
        plugin(
            ids.android.app.core,
            pkg("android.AndroidAppConvention")
        )
        plugin(
            ids.android.app.compose,
            pkg("android.AndroidAppComposeConvention")
        )
        plugin(
            ids.android.library.core,
            pkg("android.AndroidLibraryConvention")
        )
        plugin(
            ids.android.library.compose,
            pkg("android.AndroidLibraryComposeConvention")
        )
        plugin(
            ids.kmp.library,
            pkg("multiplatform.KMPLibraryConvention")
        )
        plugin(
            ids.kmp.sqldelight,
            pkg("multiplatform.SqlDelightConvention")
        )
        plugin(
            ids.jvm.app,
            pkg("jvm.JvmAppConvention")
        )
    }
}

fun NamedDomainObjectContainer<PluginDeclaration>.plugin(
    name: Provider<out PluginDependency>, plugin: String
) {
    val pluginId = name.get().pluginId
    register(pluginId) {
        id = pluginId
        implementationClass = plugin
    }
}

fun pkg(className: String) = "cz.lastaapps.plugin.$className"
