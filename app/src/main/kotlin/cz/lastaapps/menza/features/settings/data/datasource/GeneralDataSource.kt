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

package cz.lastaapps.menza.features.settings.data.datasource

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import cz.lastaapps.api.core.domain.model.DataLanguage
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType
import cz.lastaapps.menza.features.settings.domain.model.DarkMode
import cz.lastaapps.menza.features.settings.domain.model.DishListMode
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalSettingsApi::class)
@JvmInline
internal value class GeneralSettings(
    val settings: FlowSettings,
) {
    @OptIn(ExperimentalSettingsImplementation::class)
    companion object {
        private val Context.store by preferencesDataStore("menza_general_store")

        fun create(context: Context) = GeneralSettings(DataStoreSettings(context.store))
    }
}

internal interface GeneralDataSource {
    suspend fun storeAppSetupFinished()

    fun isAppSetupFinished(): Flow<Boolean>

    suspend fun storeSettingsEverOpened()

    fun isSettingsEverOpened(): Flow<Boolean>

    suspend fun setPriceType(type: PriceType)

    fun getPriceType(): Flow<PriceType>

    suspend fun setDarkMode(mode: DarkMode)

    fun getDarkMode(): Flow<DarkMode>

    suspend fun setAppTheme(theme: AppThemeType)

    fun getAppTheme(): Flow<AppThemeType?>

    suspend fun setImageScale(scale: Float)

    fun getImageScale(): Flow<Float?>

    suspend fun setImagesOnMetered(enabled: Boolean)

    fun getImagesOnMetered(): Flow<Boolean>

    suspend fun setDishLanguage(language: DataLanguage)

    fun getDishLanguage(): Flow<DataLanguage?>

    suspend fun setCompactTodayView(mode: DishListMode)

    fun isCompactTodayView(): Flow<DishListMode?>

    suspend fun setOliverRow(isUsed: Boolean)

    fun isOliverRow(): Flow<Boolean?>

    suspend fun setBalanceWarningThreshold(threshold: Int)

    fun getBalanceWarningThreshold(): Flow<Int?>

    suspend fun setAlternativeNavigation(enabled: Boolean)

    fun getAlternativeNavigation(): Flow<Boolean?>

    suspend fun setDishListModeChosen(isChosen: Boolean)

    fun getDishListModeChosen(): Flow<Boolean?>
}

@OptIn(ExperimentalSettingsApi::class)
internal class GeneralDataSourceImpl(
    generalSettings: GeneralSettings,
) : GeneralDataSource {
    private val settings = generalSettings.settings

    @Suppress("ConstPropertyName")
    companion object {
        private const val appSetupFinishedKey = "app_setup_finished"
        private const val settingsEverOpenedKey = "settings_ever_opened"
        private const val priceTypeKey = "price_type"
        private const val darkModeKey = "dark_mode"
        private const val appThemeKey = "app_theme"
        private const val imageScaleKey = "image_scale"
        private const val imagesOnMeteredKey = "images_on_metered"
        private const val dishLanguageKey = "dish_language"
        private const val compactTodayViewKey = "compact_today_view"
        private const val oliverRowsKey = "oliver_row"
        private const val balanceWarningThresholdKey = "balance_warning_threshold"
        private const val alternativeNavigationKey = "alternative_navigation"
        private const val dishListModeChosenKey = "dish_list_mode_chosen"
    }

    override suspend fun storeAppSetupFinished() = settings.putBoolean(appSetupFinishedKey, true)

    override fun isAppSetupFinished(): Flow<Boolean> = settings.getBooleanFlow(appSetupFinishedKey, false)

    override suspend fun storeSettingsEverOpened() = settings.putBoolean(settingsEverOpenedKey, true)

    override fun isSettingsEverOpened(): Flow<Boolean> = settings.getBooleanFlow(settingsEverOpenedKey, false)

    override suspend fun setPriceType(type: PriceType) = settings.putInt(priceTypeKey, type.id)

    override fun getPriceType(): Flow<PriceType> =
        settings.getIntOrNullFlow(priceTypeKey).map {
            when (it) {
                PriceType.Discounted.id -> PriceType.Discounted
                PriceType.Normal.id -> PriceType.Normal
                else -> PriceType.Unset
            }
        }

    override suspend fun setDarkMode(mode: DarkMode) = settings.putInt(darkModeKey, mode.id)

    override fun getDarkMode(): Flow<DarkMode> =
        settings.getIntOrNullFlow(darkModeKey).map { id ->
            DarkMode.entries.firstOrNull { it.id == id } ?: DarkMode.System
        }

    override suspend fun setAppTheme(theme: AppThemeType) = settings.putInt(appThemeKey, theme.id)

    override fun getAppTheme(): Flow<AppThemeType?> =
        settings.getIntOrNullFlow(appThemeKey).map { id ->
            AppThemeType.entries.firstOrNull { type -> type.id == id }
        }

    override suspend fun setImageScale(scale: Float) = settings.putFloat(imageScaleKey, scale)

    override fun getImageScale(): Flow<Float?> = settings.getFloatOrNullFlow(imageScaleKey)

    override suspend fun setImagesOnMetered(enabled: Boolean) = settings.putBoolean(imagesOnMeteredKey, enabled)

    override fun getImagesOnMetered(): Flow<Boolean> = settings.getBooleanFlow(imagesOnMeteredKey, true)

    override suspend fun setDishLanguage(language: DataLanguage) = settings.putInt(dishLanguageKey, language.id)

    override fun getDishLanguage(): Flow<DataLanguage?> =
        settings.getIntOrNullFlow(dishLanguageKey).map { id ->
            DataLanguage.entries.firstOrNull { it.id == id }
        }

    override suspend fun setCompactTodayView(mode: DishListMode) = settings.putInt(compactTodayViewKey, mode.id)

    override fun isCompactTodayView(): Flow<DishListMode?> =
        settings
            .getIntOrNullFlow(compactTodayViewKey)
            .map { id ->
                DishListMode.entries.firstOrNull { it.id == id }
            }

    override suspend fun setOliverRow(isUsed: Boolean) = settings.putBoolean(oliverRowsKey, isUsed)

    override fun isOliverRow(): Flow<Boolean?> = settings.getBooleanOrNullFlow(oliverRowsKey)

    override suspend fun setBalanceWarningThreshold(threshold: Int) = settings.putInt(balanceWarningThresholdKey, threshold)

    override fun getBalanceWarningThreshold(): Flow<Int?> = settings.getIntOrNullFlow(balanceWarningThresholdKey)

    override suspend fun setAlternativeNavigation(enabled: Boolean) = settings.putBoolean(alternativeNavigationKey, enabled)

    override fun getAlternativeNavigation(): Flow<Boolean?> = settings.getBooleanOrNullFlow(alternativeNavigationKey)

    override suspend fun setDishListModeChosen(isChosen: Boolean) = settings.putBoolean(dishListModeChosenKey, isChosen)

    override fun getDishListModeChosen(): Flow<Boolean?> = settings.getBooleanOrNullFlow(dishListModeChosenKey)
}
