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

package cz.lastaapps.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

abstract class BasePlugin(
    private val configuration: Project.() -> Unit,
    private val kmpConfiguration: Project.() -> Unit = {},
    private val androidConfiguration: Project.() -> Unit = {},
) : Plugin<Project> {
    final override fun apply(project: Project) {
        configuration(project)

        when (project.extensions.findByName("kotlin")) {
            is KotlinMultiplatformExtension -> {
                kmpConfiguration(project)
            }

            is KotlinAndroidExtension,
            // null, // The base AGP does not provide propper kotlin extension
            -> {
                androidConfiguration(project)
            }

            else -> {}
        }
    }
}
