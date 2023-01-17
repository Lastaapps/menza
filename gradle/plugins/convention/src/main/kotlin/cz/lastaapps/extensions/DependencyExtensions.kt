/*
 *    Copyright 2023, Petr Laštovička as Lasta apps, All rights reserved
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

import com.android.build.gradle.BaseExtension
import org.gradle.api.Action
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

fun DependencyHandlerScope.implementation(dependencyNotation: Any) =
    add(Constants.IMPLEMENTATION, dependencyNotation)

fun DependencyHandlerScope.commonImplementation(dependencyNotation: Any) =
    add(Constants.COMMON_IMPLEMENTATION, dependencyNotation)

fun DependencyHandlerScope.testImplementation(dependencyNotation: Any) =
    add(Constants.TEST_IMPLEMENTATION, dependencyNotation)

fun DependencyHandlerScope.api(dependencyNotation: Any) = add(Constants.API, dependencyNotation)
fun DependencyHandlerScope.coreLibraryDesugaring(dependencyNotation: Any) =
    add(Constants.DESUGARING, dependencyNotation)

fun DependencyHandlerScope.ksp(dependencyNotation: Any) =
    add(Constants.KSP, dependencyNotation)

fun BaseExtension.kotlinOptions(configure: Action<KotlinJvmOptions>) =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("kotlinOptions", configure)
