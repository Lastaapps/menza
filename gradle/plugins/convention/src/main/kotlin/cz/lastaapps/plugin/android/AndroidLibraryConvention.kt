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

package cz.lastaapps.plugin.android

import com.android.build.api.dsl.LibraryExtension
import cz.lastaapps.extensions.alias
import cz.lastaapps.extensions.implementation
import cz.lastaapps.extensions.libs
import cz.lastaapps.extensions.pluginManager
import cz.lastaapps.plugin.BasePlugin
import cz.lastaapps.plugin.android.common.KotlinBaseConvention
import cz.lastaapps.plugin.android.config.configureComposeCompiler
import cz.lastaapps.plugin.android.config.configureKotlinAndroid
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies


class AndroidLibraryConvention : BasePlugin({
    pluginManager {
        alias(libs.plugins.android.library)

        // to prevent conflicts with the KMP plugin
        if (extensions.findByName("kotlin") == null)
            alias(libs.plugins.kotlin.android)
    }

    extensions.configure<LibraryExtension> {
        configureKotlinAndroid(this)
        configureComposeCompiler(this)
        defaultConfig {
            multiDexEnabled = true
        }

        buildFeatures {
            buildConfig = false
        }
    }
    dependencies {
        implementation(libs.androidx.compose.runtime)
    }

    apply<KotlinBaseConvention>()
    apply<AndroidKoinConvention>()
    apply<AndroidBaseConvention>()
})
