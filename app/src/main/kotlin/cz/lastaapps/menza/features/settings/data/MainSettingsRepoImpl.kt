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

package cz.lastaapps.menza.features.settings.data

import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.menza.features.settings.data.datasource.GeneralDataSource
import cz.lastaapps.menza.features.settings.data.datasource.InitMenzaDataSource
import cz.lastaapps.menza.features.settings.domain.MainSettingsRepo
import kotlinx.coroutines.flow.Flow

internal class MainSettingsRepoImpl(
    private val initial: InitMenzaDataSource,
    private val general: GeneralDataSource,
) : MainSettingsRepo {
    override suspend fun storeLatestMenza(type: MenzaType) =
        initial.storeLatestMenza(type)

    override fun getLatestMenza(): Flow<MenzaType?> =
        initial.getLatestMenza()

    override suspend fun storePreferredMenza(type: MenzaType) =
        initial.storePreferredMenza(type)

    override fun getPreferredMenza(): Flow<MenzaType?> =
        initial.getPreferredMenza()

    override suspend fun storeAppSetupFinished() =
        general.storeAppSetupFinished()

    override fun isAppSetupFinished(): Flow<Boolean> =
        general.isAppSetupFinished()
}
