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

import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose.compiler) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.atomicfu) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.aboutLibraries) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.detekt) apply false

    alias(libs.plugins.versionCatalogUpdate)
    alias(libs.plugins.ktlint)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

versionCatalogUpdate {
    sortByKey.set(true)
    // pins version - wouldn't be changed
    pin {}
    // keeps entry - wouldn't be deleted when unused
    keep {
        keepUnusedVersions.set(true)
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("rc", "beta", "release").any { version.lowercase().contains(it) }
    val regex = """^[0-9,.v-]+(-r)?$""".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

extensions.configure<KtlintExtension> {
    val dir = "${layout.buildDirectory.get().asFile.absolutePath}/generated/"
    filter {
        exclude {
            it.file.path.startsWith(dir)
        }
    }
}
