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

package cz.lastaapps.extensions

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.the
import org.gradle.plugin.use.PluginDependency
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

val Project.libs get() = the<org.gradle.accessors.dm.LibrariesForLibs>()

fun PluginManager.alias(plugin: Provider<PluginDependency>) {
    apply(plugin.get().pluginId)
}

val Project.multiplatform: KotlinMultiplatformExtension
    get() = kotlinExtension as KotlinMultiplatformExtension

fun Project.multiplatform(block: KotlinMultiplatformExtension.() -> Unit) {
    multiplatform.apply(block)
}

fun Project.pluginManager(block: PluginManager.() -> Unit) {
    pluginManager.apply(block)
}

fun Project.android(block: CommonExtension<*, *, *, *, *, *>.() -> Unit) {
    extension("android", block)
}

fun Project.androidApp(block: BaseAppModuleExtension.() -> Unit) {
    extension("android", block)
}

fun Project.androidLibrary(block: LibraryExtension.() -> Unit) {
    extension("android", block)
}

fun Project.java(block: JavaPluginExtension.() -> Unit) {
    extension("java", block)
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
fun Project.compilerOptions(
    jvmAndroid: KotlinJvmCompilerOptions.() -> Unit = {},
    common: KotlinCommonCompilerOptions.() -> Unit,
) {
    var anySucceed = false

    extensions
        .findByType<KotlinAndroidProjectExtension>()
        ?.apply {
            this.compilerOptions {
                common()
                jvmAndroid()
            }
        }?.also { anySucceed = true }

    extensions
        .findByType<KotlinJvmProjectExtension>()
        ?.apply {
            this.compilerOptions {
                common()
                jvmAndroid()
            }
        }?.also { anySucceed = true }

    extensions
        .findByType<KotlinMultiplatformExtension>()
        ?.apply {
            this.compilerOptions {
                common()
            }
        }?.also { anySucceed = true }

    if (!anySucceed) {
        // this will crash
        error("No available Kotlin extension found")
    }
}

inline fun <reified T : Any> Project.extension(
    name: String,
    block: Action<T>,
) {
    extensions.configure(name, block)
//    extensions.findByName(name)?.let { it as? T }?.apply(block)
//        ?: error("Extension $name missing")
}
