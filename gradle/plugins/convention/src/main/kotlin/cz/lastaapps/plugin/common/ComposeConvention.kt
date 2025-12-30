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

import cz.lastaapps.extensions.alias
import cz.lastaapps.extensions.libs
import cz.lastaapps.extensions.multiplatform
import cz.lastaapps.extensions.pluginManager
import cz.lastaapps.plugin.BasePlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

abstract class ComposeConvention(
    mainDependencies: (KotlinDependencyHandler).(Project) -> Unit,
    androidDependencies: (KotlinDependencyHandler).(Project) -> Unit,
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

            multiplatform {
                sourceSets.commonMain.dependencies {
                    mainDependencies(project)
                }
                sourceSets.androidMain.dependencies {
                    androidDependencies(project)
                }
            }
        },
    )

class ComposeUIConvention :
    ComposeConvention(
        { with(it) { dependenciesComposeUI() } },
        {
            with(it) {
                dependenciesAndroidComposeUI()
            }
        },
    )

class ComposeRuntimeConvention :
    ComposeConvention(
        {},
        { with(it) { implementation(libs.androidx.compose.runtime) } },
    )

context(p: Project)
private fun KotlinDependencyHandler.dependenciesComposeUI() {
    implementation(p.libs.androidx.compose.material3)
    implementation(p.libs.androidx.compose.material3WindowSizeClass)
    implementation(p.libs.androidx.compose.iconsCore)
    implementation(p.libs.androidx.compose.iconsExtended)
    implementation(p.libs.androidx.compose.animation)
    implementation(p.libs.androidx.compose.ui.util)
    // TODO 9.0
    //    dependencies {
    //        "androidRuntimeClasspath"(libs.androidx.compose.ui.tooling)
    //    }
    implementation(p.libs.androidx.compose.tooling)
    implementation(p.libs.androidx.compose.toolingPreview)

    implementation(p.libs.decompose.core)
    implementation(
        p.libs.decompose.compose
            .asProvider(),
    )
    implementation(p.libs.decompose.compose.experimental)

    implementation(p.libs.coil.compose.complete)
}

context(p: Project)
private fun KotlinDependencyHandler.dependenciesAndroidComposeUI() {
    implementation(p.libs.androidx.activity.compose)

    implementation(
        p.libs.androidx.lifecycle.runtime
            .asProvider(),
    )
    implementation(p.libs.androidx.lifecycle.runtime.compose)
}
