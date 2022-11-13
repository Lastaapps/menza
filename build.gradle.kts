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

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
    dependencies {}
}

group = App.GROUP
version = App.VERSION_NAME

plugins {
    val gradleVersion = "7.3.1"
    id(Plugins.APPLICATION) version gradleVersion apply false
    id(Plugins.LIBRARY) version gradleVersion apply false
    id(Plugins.KOTLIN_ANDROID) version Versions.KOTLIN apply false
    id(Plugins.KOTLIN_MULTIPLATFORM) version Versions.KOTLIN apply false
    id(Plugins.SERIALIZATION) version Versions.KOTLIN apply false
    id(Plugins.SQLDELIGHT) version Versions.SQLDELIGHT apply false
    id(Plugins.KSP) version Versions.KSP apply false
    id(Plugins.ABOUT_LIBRARIES) version Versions.ABOUT_LIBRARIES apply false

    id("com.github.ben-manes.versions") version "0.44.0"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
//
//versionCatalogUpdate {
//    sortByKey.set(true)
//    // pins version - wouldn't be changed
//    pin {}
//    // keeps entry - wouldn't be deleted when unused
//    keep {
//        keepUnusedVersions.set(true)
//        keepUnusedLibraries.set(true)
//        keepUnusedPlugins.set(true)
//    }
//}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("rc", "beta", "release").any { version.toUpperCase().contains(it) }
    val regex = """^[0-9,.v-]+(-r)?$""".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
