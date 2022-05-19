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

package cz.lastaapps.crash

import android.content.Context
import androidx.annotation.Keep
import androidx.startup.Initializer
import cz.lastaapps.crash.di.HiltInitializer
import cz.lastaapps.crash.di.InitializerEntryPoint
import org.lighthousegames.logging.logging
import javax.inject.Inject

@Keep
internal class StartInit : Initializer<Unit> {

    companion object {
        private val log = logging()
    }

    @Inject
    lateinit var database: CrashDatabase

    override fun create(context: Context) {
        log.i { "Initializing crash storage" }
        InitializerEntryPoint.resolve(context).inject(this)
        Catcher.register(database)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        HiltInitializer::class.java,
    )
}
