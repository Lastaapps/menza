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

package cz.lastaapps.api.buffet.data

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import cz.lastaapps.api.buffet.BuffetDatabase
import org.koin.core.scope.Scope

internal const val DB_NAME = "buffet_api.db"

internal fun Scope.createBuffetDBDriver() = BuffetDatabaseSqlDriver(AndroidSqliteDriver(BuffetDatabase.Schema, get(), DB_NAME))
