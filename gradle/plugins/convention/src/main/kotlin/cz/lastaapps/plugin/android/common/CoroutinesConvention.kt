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

package cz.lastaapps.plugin.android.common

import cz.lastaapps.extensions.implementation
import cz.lastaapps.extensions.libs
import cz.lastaapps.extensions.testImplementation
import cz.lastaapps.plugin.BasePlugin
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class CoroutinesConvention : BasePlugin({
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            @Suppress("SuspiciousCollectionReassignment")
            freeCompilerArgs += listOf(
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview"
            )
        }
    }

    dependencies {
        implementation(libs.kotlin.coroutines.common)
        implementation(libs.kotlin.coroutines.android)
        testImplementation(libs.kotlin.coroutines.test)
    }
})
