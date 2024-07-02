/*
 *    Copyright 2024, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.extensions

import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.implementation(
    dependency: String,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit = {},
) = Constants.IMPLEMENTATION(dependency, dependencyConfiguration)

fun <T : Any> DependencyHandlerScope.implementation(
    dependency: Provider<T>,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit = {},
) = Constants.IMPLEMENTATION(dependency, dependencyConfiguration)

fun <T : Any> DependencyHandlerScope.commonImplementation(
    dependency: Provider<T>,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit = {},
) = Constants.COMMON_IMPLEMENTATION(dependency, dependencyConfiguration)

fun <T : Any> DependencyHandlerScope.testImplementation(
    dependency: Provider<T>,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit = {},
) = Constants.TEST_IMPLEMENTATION(dependency, dependencyConfiguration)

fun <T : Any> DependencyHandlerScope.debugImplementation(
    dependency: Provider<T>,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit = {},
) = Constants.DEBUG_IMPLEMENTATION(dependency, dependencyConfiguration)

fun <T : Any> DependencyHandlerScope.api(
    dependency: Provider<T>,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit = {},
) = Constants.API(dependency, dependencyConfiguration)

fun DependencyHandlerScope.coreLibraryDesugaring(dependencyNotation: Any) =
    add(Constants.DESUGARING, dependencyNotation)

fun DependencyHandlerScope.ksp(dependencyNotation: Any) =
    add(Constants.KSP, dependencyNotation)
