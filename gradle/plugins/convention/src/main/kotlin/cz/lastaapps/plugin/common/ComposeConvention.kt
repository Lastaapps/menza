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

package cz.lastaapps.plugin.common

import cz.lastaapps.plugin.BasePlugin
import cz.lastaapps.plugin.extensions.alias
import cz.lastaapps.plugin.extensions.debugImplementation
import cz.lastaapps.plugin.extensions.implementation
import cz.lastaapps.plugin.extensions.libs
import cz.lastaapps.plugin.extensions.multiplatform
import cz.lastaapps.plugin.extensions.pluginManager
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

/**
 * Provide base common compose configuration for module.
 * Dependencies depend on the way Compose is used.
 */
abstract class ComposeConvention(
    mainDependencies: Project.() -> List<Provider<MinimalExternalModuleDependency>>,
    androidMainDependencies: Project.() -> List<Provider<MinimalExternalModuleDependency>>,
    androidDebugDependencies: Project.() -> List<Provider<MinimalExternalModuleDependency>>,
) : BasePlugin(
        {
            pluginManager {
                alias(libs.plugins.kotlin.compose.compiler)
                // Required by Decompose
                alias(libs.plugins.kotlin.serialization)
            }

            with(extensions.getByType<ComposeCompilerGradlePluginExtension>()) {
                includeSourceInformation = true
                featureFlags = setOf()
            }
        },
        {
            multiplatform {
                sourceSets.commonMain.dependencies {
                    mainDependencies().forEach(::implementation)
                }
                sourceSets.androidMain.dependencies {
                    androidMainDependencies().forEach(::implementation)
                    androidDebugDependencies().forEach(::implementation)
                }
            }
            dependencies {
                // Removes tooling package from the release variant
                "androidRuntimeClasspath"(libs.androidx.compose.tooling)
                "androidRuntimeClasspath"(libs.androidx.compose.toolingPreview)
            }
        },
        {
            dependencies {
                mainDependencies().forEach(::implementation)
                androidMainDependencies().forEach(::implementation)
                androidDebugDependencies().forEach(::debugImplementation)
            }
        },
    )

/**
 * Provides dependencies to use compose as a regular UI framework.
 */
class ComposeUIConvention :
    ComposeConvention(
        { dependenciesComposeUI() },
        { dependenciesAndroidComposeUI() },
        { dependenciesDebug() },
    )

/**
 * Provides base dependencies mostly for @Stable and @Immutable annotations
 */
class ComposeRuntimeConvention :
    ComposeConvention(
        { emptyList() },
        { listOf(libs.androidx.compose.runtime) },
        { emptyList() },
    )

private fun Project.dependenciesComposeUI(): List<Provider<MinimalExternalModuleDependency>> =
    listOf(
        libs.androidx.compose.material3,
        libs.androidx.compose.material3WindowSizeClass,
        libs.androidx.compose.iconsCore,
        libs.androidx.compose.iconsExtended,
        libs.androidx.compose.animation,
        libs.androidx.compose.ui.util,
        libs.decompose.core,
        libs.decompose.compose
            .asProvider(),
        libs.decompose.compose.experimental,
        libs.coil.compose.complete,
        libs.coil.complete,
        libs.coil.gif,
        libs.coil.network.ktor,
        libs.coil.svg,
    )

private fun Project.dependenciesDebug(): List<Provider<MinimalExternalModuleDependency>> =
    listOf(
        libs.androidx.compose.tooling,
        libs.androidx.compose.toolingPreview,
    )

private fun Project.dependenciesAndroidComposeUI(): List<Provider<MinimalExternalModuleDependency>> =
    listOf(
        libs.androidx.activity.compose,
        libs.androidx.lifecycle.runtime
            .asProvider(),
        libs.androidx.lifecycle.runtime.compose,
    )
