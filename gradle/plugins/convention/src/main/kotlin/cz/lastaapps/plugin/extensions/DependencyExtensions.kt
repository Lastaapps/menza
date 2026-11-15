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

@file:Suppress("UnstableApiUsage")

package cz.lastaapps.plugin.extensions

import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope

/*
 * Provide save call interface in project.dependencies { } block
 */

fun <T : Any> DependencyHandlerScope.implementation(
    dependency: Provider<T>,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit = {},
) = "implementation"(dependency, dependencyConfiguration)

fun <T : Any> DependencyHandlerScope.testImplementation(
    dependency: Provider<T>,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit = {},
) = "testImplementation"(dependency, dependencyConfiguration)

fun <T : Any> DependencyHandlerScope.debugImplementation(
    dependency: Provider<T>,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit = {},
) = "debugImplementation"(dependency, dependencyConfiguration)

fun <T : Any> DependencyHandlerScope.api(
    dependency: Provider<T>,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit = {},
) = "api"(dependency, dependencyConfiguration)

fun DependencyHandlerScope.coreLibraryDesugaring(dependencyNotation: Any) = add("coreLibraryDesugaring", dependencyNotation)

fun DependencyHandlerScope.ksp(dependencyNotation: Any) = add("ksp", dependencyNotation)
