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

package cz.lastaapps.menza.features.panels

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.features.panels.aprilfools.ui.AprilFools
import cz.lastaapps.menza.features.panels.aprilfools.ui.shouldShowAprilFools
import cz.lastaapps.menza.features.panels.crashreport.ui.CrashReport
import cz.lastaapps.menza.features.panels.crashreport.ui.CrashesViewModel
import cz.lastaapps.menza.features.panels.rateus.ui.RateUsPanel
import cz.lastaapps.menza.features.panels.rateus.ui.RateUsViewModel
import cz.lastaapps.menza.features.panels.whatsnew.ui.WhatsNewPanel
import cz.lastaapps.menza.features.panels.whatsnew.ui.vm.WhatsNewViewModel
import cz.lastaapps.menza.features.panels.whatsnew.ui.vm.whatsNewViewModel
import cz.lastaapps.menza.ui.locals.koinActivityViewModel
import cz.lastaapps.menza.ui.util.HandleError
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun Panels(
    hostState: SnackbarHostState,
    crashesViewModel: CrashesViewModel,
    whatsNewViewModel: WhatsNewViewModel,
    rateUsViewModel: RateUsViewModel,
    modifier: Modifier = Modifier,
) {
    HandleAppear(appearing = whatsNewViewModel)
    HandleAppear(appearing = rateUsViewModel)
    HandleError(holder = rateUsViewModel, hostState = hostState)

    Box(modifier.animateContentSize()) {
        val showCrash = crashesViewModel.flowState.value.hasUnreported
        val showWhatsNew = whatsNewViewModel.flowState.value.shouldShow
        val showRateUs = rateUsViewModel.flowState.value.shouldShow
        val showAprils = shouldShowAprilFools()

        val items = remember(showCrash, showWhatsNew, showRateUs, showAprils) {
            persistentListOf(
                PanelItem(showCrash) {
                    CrashReport(
                        crashesViewModel.flowState.value,
                        crashesViewModel::makeReported,
                        it,
                    )
                },
                PanelItem(showWhatsNew) { WhatsNewPanel(whatsNewViewModel, it) },
                PanelItem(showRateUs) { RateUsPanel(rateUsViewModel, it) },
                PanelItem(showAprils) { AprilFools(it) },
            )
        }

        Card(
            colors = CardDefaults.cardColors(),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.animateContentSize(),
        ) {
            items
                .firstOrNull { it.shouldShow }
                ?.content
                ?.let { content ->
                    content(
                        Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                    )
                }
        }
    }
}

private data class PanelItem(
    val shouldShow: Boolean,
    val content: @Composable (Modifier) -> Unit,
)