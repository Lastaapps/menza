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

package cz.lastaapps.menza.util

import android.content.Context
import androidx.annotation.Keep
import androidx.startup.AppInitializer
import androidx.startup.Initializer
import cz.lastaapps.core.util.extensions.localLogger
import cz.lastaapps.crash.StartInit
import org.koin.androix.startup.KoinInitializer
import org.koin.core.annotation.KoinExperimentalAPI

@Keep
@Suppress("unused")
internal class ReEnableCrashInit : Initializer<Unit> {
    private val log = localLogger()

    override fun create(context: Context) {
        log.d { "Starting" }
        AppInitializer
            .getInstance(context)
            .initializeComponent(StartInit::class.java)
    }

    @OptIn(KoinExperimentalAPI::class)
    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(KoinInitializer::class.java)
}
