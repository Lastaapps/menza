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

package cz.lastaapps.crash

import cz.lastaapps.crash.entity.Crash
import cz.lastaapps.crash.entity.ErrorSeverity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

class Catcher(
    private val database: CrashDatabase,
    private val default: Thread.UncaughtExceptionHandler?,
) : Thread.UncaughtExceptionHandler {
    companion object {
        fun register(database: CrashDatabase) {
            val defaultEH = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler(Catcher(database, defaultEH))
        }
    }

    override fun uncaughtException(
        t: Thread,
        e: Throwable,
    ) {
        try {
            runBlocking(Dispatchers.IO) {
                with(Crash.fromError(e, ErrorSeverity.CRASH)) {
                    database.crashQueries.saveCrash(
                        date,
                        severity,
                        message,
                        trace,
                        reported,
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        default?.uncaughtException(t, e) ?: exitProcess(2)
    }
}
