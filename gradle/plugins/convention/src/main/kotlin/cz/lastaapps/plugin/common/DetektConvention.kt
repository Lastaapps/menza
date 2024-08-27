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

package cz.lastaapps.plugin.common

import cz.lastaapps.extensions.alias
import cz.lastaapps.extensions.libs
import cz.lastaapps.extensions.pluginManager
import cz.lastaapps.plugin.BasePlugin
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class DetektConvention :
    BasePlugin(
        {
            pluginManager {
                alias(libs.plugins.detekt)
            }

            extensions.configure<DetektExtension>("detekt") {
                buildUponDefaultConfig = true
                allRules = true
                parallel = true
                config.setFrom(files("$projectDir/config/detekt.yml"))
                baseline = file("$rootDir/gradle/detekt-baseline.xml")
            }

            tasks.withType<Detekt>().configureEach {
                reports {
                    html.required.set(false)
                    xml.required.set(false)
                    txt.required.set(false)
                    sarif.required.set(false)
                    md.required.set(false)
                }
            }

            tasks.withType<Detekt>().configureEach {
                jvmTarget =
                    libs.versions.java.jvmTarget
                        .get()
            }
            tasks.withType<DetektCreateBaselineTask>().configureEach {
                jvmTarget =
                    libs.versions.java.jvmTarget
                        .get()
            }

            dependencies {
                add("detektPlugins", libs.detekt.kode.compose)
                add("detektPlugins", libs.detekt.twitter.compose)
            }
        },
    )
