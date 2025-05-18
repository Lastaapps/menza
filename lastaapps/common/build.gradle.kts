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

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.lastaapps.android.library)
    alias(libs.plugins.lastaapps.common.compose)
    `maven-publish`
}

android {
    namespace = "cz.lastaapps.common"

    defaultConfig {
        buildConfigField("java.lang.String", "BUILD_DATE", "\"${formatDate(modificationTime())}\"")
    }
    buildTypes {
        debug {
            buildConfigField("java.lang.String", "BUILD_DATE", "\"${formatDate()}\"")
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.google.material)
}

private fun formatDate(instant: Instant = Instant.now()) =
    LocalDateTime
        .ofInstant(instant, UTC)
        .format(DateTimeFormatter.ISO_DATE)

// https://reproducible-builds.org/docs/source-date-epoch/
// Set based on the current commit that is being built.
private fun modificationTime() =
    (
        System
            .getenv("SOURCE_DATE_EPOCH")
            ?.takeIf { it.isNotBlank() }
            ?.toLong()
            ?: ProcessBuilder("git", "log", "-1", "--format=%ct")
                .directory(rootDir)
                .redirectErrorStream(true)
                .start()
                .let { process ->
                    check(process.waitFor() == 0) { "git log failed" }
                    process.inputStream
                        .bufferedReader()
                        .use { it.readText() }
                        .trim()
                        .also { check(it.isNotBlank()) { "git log did not return a timestamp" } }
                        .toLong()
                }
    ).let(Instant::ofEpochSecond)
