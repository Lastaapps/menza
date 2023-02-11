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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@OptIn(
    ExperimentalSettingsApi::class,
    ExperimentalSettingsImplementation::class,
)
@JvmInline
internal value class InitialSettings(val settings: FlowSettings) {
    companion object {
        private val Context.store by preferencesDataStore("menza_initial_store")

        fun create(context: Context) = InitialSettings(DataStoreSettings(context.store))
    }
}

internal interface InitMenzaDataSource {
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
        private const val latestPrefix = "latest_"
        private const val preferredPrefix = "preferred_"
        private const val menzaNameKey = "menza_name"
        private const val menzaIdExtraKey = "menza_id"

        @Keep
        private enum class MenzaStoreType {
            AgataStrahov, AgataSubsystem, BuffetFel, BuffetFs,
            ;
        }

        private fun MenzaType.toStoreKey() =
            when (this) {
                Strahov -> MenzaStoreType.AgataStrahov
                is Subsystem -> MenzaStoreType.AgataSubsystem
                FEL -> MenzaStoreType.BuffetFel
                FS -> MenzaStoreType.BuffetFs
            }
    }

    private val settings = initialSettings.settings

    override suspend fun storeLatestMenza(type: MenzaType) = storeMenza(latestPrefix, type)
    override fun getLatestMenza(): Flow<MenzaType?> = getMenza(latestPrefix)
    override suspend fun storePreferredMenza(type: MenzaType) = storeMenza(preferredPrefix, type)
    override fun getPreferredMenza(): Flow<MenzaType?> = getMenza(preferredPrefix)

    private suspend fun storeMenza(prefix: String, type: MenzaType) {
        settings.putString(prefix + menzaNameKey, type.toStoreKey().name)
        when (type) {
            is Subsystem -> settings.putInt(prefix + menzaIdExtraKey, type.subsystemId)
            else -> {}
        }
    }

    private fun getMenza(prefix: String): Flow<MenzaType?> =
        combine(
            settings.getStringOrNullFlow(prefix + menzaNameKey),
            settings.getIntOrNullFlow(prefix + menzaIdExtraKey)
        ) { name, id ->
            val type = MenzaStoreType.values()
                .firstOrNull { it.name == name } ?: return@combine null
            when (type) {
                MenzaStoreType.AgataStrahov -> Strahov
                MenzaStoreType.AgataSubsystem -> Subsystem(id ?: return@combine null)
                MenzaStoreType.BuffetFel -> FEL
                MenzaStoreType.BuffetFs -> FS
            }
        }
}
