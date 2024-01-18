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

package cz.lastaapps.menza.features.starting.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.pages.Pages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import cz.lastaapps.menza.features.settings.ui.component.AppThemeContent
import cz.lastaapps.menza.features.settings.ui.component.ReorderMenzaContent
import cz.lastaapps.menza.features.starting.ui.component.AllSetContent
import cz.lastaapps.menza.features.starting.ui.component.DownloadContent
import cz.lastaapps.menza.features.starting.ui.component.PolicyContent
import cz.lastaapps.menza.features.starting.ui.component.PriceTypeContent
import cz.lastaapps.menza.features.starting.ui.navigation.StartingComponent.Child.AllSet
import cz.lastaapps.menza.features.starting.ui.navigation.StartingComponent.Child.ChoosePrice
import cz.lastaapps.menza.features.starting.ui.navigation.StartingComponent.Child.ChooseTheme
import cz.lastaapps.menza.features.starting.ui.navigation.StartingComponent.Child.DownloadData
import cz.lastaapps.menza.features.starting.ui.navigation.StartingComponent.Child.OrderMenzaList
import cz.lastaapps.menza.features.starting.ui.navigation.StartingComponent.Child.Policy

@OptIn(ExperimentalDecomposeApi::class, ExperimentalFoundationApi::class)
@Composable
internal fun StartingContent(
    component: StartingComponent,
    modifier: Modifier = Modifier,
    onDone: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
    ) {
        val pager = component.content.subscribeAsState()

        val childModifier = Modifier.padding(it)
        Pages(
            pages = pager,
            onPageSelected = { /* TODO separate download and privacy policy, enable this after */ },
            scrollAnimation = PagesScrollAnimation.Default,
        ) { _, page ->
            val next = component::next
            when (page) {
                is AllSet -> AllSetContent(page.component, childModifier, onDone)
                is ChoosePrice -> PriceTypeContent(page.component, childModifier, next)
                is ChooseTheme -> AppThemeContent(page.component, next)
                is DownloadData -> DownloadContent(page.component, childModifier, next)
                is OrderMenzaList -> ReorderMenzaContent(page.component, onDone = next)
                is Policy -> PolicyContent(page.component, childModifier, next)
            }
        }
    }
}
