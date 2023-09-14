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

pluginManagement {
    includeBuild("gradle/plugins")
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "Menza"

includeBuild("appyx") {
    dependencySubstitution {
        substitute(module("lib.stolen:appyx-interactions")).using(project(":appyx-interactions:appyx-interactions"))
        substitute(module("lib.stolen:appyx-components:backstack")).using(project(":appyx-components:stable:backstack:backstack"))
        substitute(module("lib.stolen:appyx-components:spotlight")).using(project(":appyx-components:stable:spotlight:spotlight"))
        substitute(module("lib.stolen:appyx-navigation")).using(project(":appyx-navigation:appyx-navigation"))
    }
}

include(
    ":api:agata",
    ":api:buffet",
    ":api:core",
    ":api:main",
    ":app",
    ":core",
    ":scraping",
    ":entity",
    ":storage:db",
    ":storage:repo",
    ":lastaapps:common",
    ":lastaapps:crash",
    ":html-parser",
)
