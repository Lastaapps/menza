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

package cz.lastaapps.menza.features.main.ui.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface MainNavType : Parcelable {
    companion object {
        val allMainTypes = listOf(
            TodayNav,
            WeekNav,
            InfoNav,
            SettingsNav,
            OsturakNav,
            PrivacyPolicyNav,
            LicenseNoticesNav,
        )
    }

    @Parcelize
    data object TodayNav : MainNavType

    @Parcelize
    data object WeekNav : MainNavType

    @Parcelize
    data object InfoNav : MainNavType

    @Parcelize
    data object SettingsNav : MainNavType

    @Parcelize
    data object OsturakNav : MainNavType

    @Parcelize
    data object PrivacyPolicyNav : MainNavType

    @Parcelize
    data object LicenseNoticesNav : MainNavType

    @Parcelize
    data object DrawerContent : MainNavType
}
