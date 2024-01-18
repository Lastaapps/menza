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

package cz.lastaapps.menza.features.main.ui.navigation

/**
 * Exists so I can keep the internal navigation implementation private
 */
internal enum class MainNavTarget {
    Today,
    Week,
    Info,
    Settings,
    Osturak,
    PrivacyPolicy,
    LicenseNotices,
    ;

    companion object {
        fun fromChild(child: MainComponent.Child) = when (child) {
            is MainComponent.Child.Info -> Info
            is MainComponent.Child.LicenseNotices -> LicenseNotices
            is MainComponent.Child.Osturak -> Osturak
            is MainComponent.Child.PrivacyPolicy -> PrivacyPolicy
            is MainComponent.Child.Settings -> Settings
            is MainComponent.Child.Today -> Today
            is MainComponent.Child.Week -> Week
        }
    }
}
