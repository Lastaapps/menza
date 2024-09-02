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

package cz.lastaapps.menza.features.settings.data.datasource

import android.content.Context
import androidx.annotation.Keep
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.MenzaType.Agata.Strahov
import cz.lastaapps.api.core.domain.model.MenzaType.Agata.Subsystem
import cz.lastaapps.api.core.domain.model.MenzaType.Buffet.FEL
import cz.lastaapps.api.core.domain.model.MenzaType.Buffet.FS
import cz.lastaapps.api.core.domain.model.MenzaType.Testing.Kocourkov
import cz.lastaapps.menza.features.settings.data.datasource.InitMenzaDataSourceImpl.Companion.MenzaStoreType.AgataStrahov
import cz.lastaapps.menza.features.settings.data.datasource.InitMenzaDataSourceImpl.Companion.MenzaStoreType.AgataSubsystem
import cz.lastaapps.menza.features.settings.data.datasource.InitMenzaDataSourceImpl.Companion.MenzaStoreType.BuffetFel
import cz.lastaapps.menza.features.settings.data.datasource.InitMenzaDataSourceImpl.Companion.MenzaStoreType.BuffetFs
import cz.lastaapps.menza.features.settings.data.datasource.InitMenzaDataSourceImpl.Companion.MenzaStoreType.TestingKocourkov
import cz.lastaapps.menza.features.settings.domain.model.InitialSelectionBehaviour
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@OptIn(
    ExperimentalSettingsApi::class,
    ExperimentalSettingsImplementation::class,
)
@JvmInline
internal value class InitialSettings(
    val settings: FlowSettings,
) {
    companion object {
        private val Context.store by preferencesDataStore("menza_initial_store")

        fun create(context: Context) = InitialSettings(DataStoreSettings(context.store))
    }
}

internal interface InitMenzaDataSource {
    suspend fun storeInitialMenzaMode(mode: InitialSelectionBehaviour)

    fun getInitialMenzaMode(): Flow<InitialSelectionBehaviour>

    suspend fun storeLatestMenza(type: MenzaType)

    fun getLatestMenza(): Flow<MenzaType?>

    suspend fun storePreferredMenza(type: MenzaType)

    fun getPreferredMenza(): Flow<MenzaType?>
}

@OptIn(ExperimentalSettingsApi::class)
internal class InitMenzaDataSourceImpl(
    initialSettings: InitialSettings,
) : InitMenzaDataSource {
    companion object {
        private const val INITIAL_MODE_KEY = "initial_mode"

        private const val LATEST_PREFIX = "latest_"
        private const val PREFERRED_PREFIX = "preferred_"
        private const val MENZA_NAME_KEY = "menza_name"
        private const val MENZA_ID_EXTRA_KEY = "menza_id"

        @Keep
        private enum class MenzaStoreType {
            AgataStrahov,
            AgataSubsystem,
            BuffetFel,
            BuffetFs,
            TestingKocourkov,
        }

        private fun MenzaType.toStoreKey() =
            when (this) {
                Strahov -> AgataStrahov
                is Subsystem -> AgataSubsystem
                FEL -> BuffetFel
                FS -> BuffetFs
                Kocourkov -> TestingKocourkov
            }
    }

    private val settings = initialSettings.settings

    override suspend fun storeInitialMenzaMode(mode: InitialSelectionBehaviour) = settings.putInt(INITIAL_MODE_KEY, mode.id)

    override fun getInitialMenzaMode(): Flow<InitialSelectionBehaviour> =
        settings
            .getIntFlow(INITIAL_MODE_KEY, 0)
            .map {
                when (it) {
                    1 -> InitialSelectionBehaviour.Remember
                    2 -> InitialSelectionBehaviour.Specific
                    else -> InitialSelectionBehaviour.Ask
                }
            }

    override suspend fun storeLatestMenza(type: MenzaType) = storeMenza(LATEST_PREFIX, type)

    override fun getLatestMenza(): Flow<MenzaType?> = getMenza(LATEST_PREFIX)

    override suspend fun storePreferredMenza(type: MenzaType) = storeMenza(PREFERRED_PREFIX, type)

    override fun getPreferredMenza(): Flow<MenzaType?> = getMenza(PREFERRED_PREFIX)

    private suspend fun storeMenza(
        prefix: String,
        type: MenzaType,
    ) {
        settings.putString(prefix + MENZA_NAME_KEY, type.toStoreKey().name)
        when (type) {
            is Subsystem -> settings.putInt(prefix + MENZA_ID_EXTRA_KEY, type.subsystemId)
            else -> {}
        }
    }

    private fun getMenza(prefix: String): Flow<MenzaType?> =
        combine(
            settings.getStringOrNullFlow(prefix + MENZA_NAME_KEY),
            settings.getIntOrNullFlow(prefix + MENZA_ID_EXTRA_KEY),
        ) { name, id ->
            val type =
                MenzaStoreType.entries
                    .firstOrNull { it.name == name } ?: return@combine null
            when (type) {
                AgataStrahov -> Strahov
                AgataSubsystem -> Subsystem(id ?: return@combine null)
                BuffetFel -> FEL
                BuffetFs -> FS
                TestingKocourkov -> Kocourkov
            }
        }
}
