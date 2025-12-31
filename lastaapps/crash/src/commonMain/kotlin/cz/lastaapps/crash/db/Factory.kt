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

package cz.lastaapps.crash.db

import app.cash.sqldelight.db.SqlDriver
import crash.Crashes
import cz.lastaapps.crash.CrashDatabase

internal data class CrashDatabaseDriver(
    val driver: SqlDriver,
)

internal fun createDatabase(driver: CrashDatabaseDriver): CrashDatabase =
    CrashDatabase(
        driver.driver,
        crashesAdapter =
            Crashes.Adapter(
                CrashAdapter.dateAdapter,
                CrashAdapter.severityAdapter,
                CrashAdapter.reportedAdapter,
            ),
    )
