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

package cz.lastaapps.menza.features.info.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.menza.features.info.ui.vm.InfoState
import cz.lastaapps.menza.features.info.ui.vm.InfoViewModel
import cz.lastaapps.menza.features.info.ui.widgets.AddressList
import cz.lastaapps.menza.features.info.ui.widgets.ContactList
import cz.lastaapps.menza.features.info.ui.widgets.LinkList
import cz.lastaapps.menza.features.info.ui.widgets.MessageList
import cz.lastaapps.menza.features.info.ui.widgets.OpeningHoursList
import cz.lastaapps.menza.features.main.ui.widgets.WrapMenzaNotSelected
import cz.lastaapps.menza.ui.components.PullToRefreshWrapper
import cz.lastaapps.menza.ui.components.layout.AboveOrSideBySideLayout
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.HandleError
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun InfoScreen(
    viewModel: InfoViewModel,
    onOsturak: () -> Unit,
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    var error by remember { mutableStateOf<DomainError?>(null) }
    HandleError(error, hostState) { error = null }

    InfoEffects(viewModel, hostState)

    val state by viewModel.flowState
    InfoContent(
        state = state,
        onRefresh = viewModel::reload,
        onOsturak = onOsturak,
        onError = { error = it },
        modifier = modifier,
    )
}

@Composable
private fun InfoEffects(
    viewModel: InfoViewModel,
    hostState: SnackbarHostState,
) {
    HandleError(viewModel, hostState)
}

@Composable
private fun InfoContent(
    state: InfoState,
    onRefresh: () -> Unit,
    onOsturak: () -> Unit,
    onError: (DomainError) -> Unit,
    modifier: Modifier = Modifier,
) {
    WrapMenzaNotSelected(
        menza = state.selectedMenza,
        onOsturak = onOsturak,
        modifier = modifier,
    ) {
        Crossfade(
            targetState = state.items,
            label = "info",
            modifier = Modifier.fillMaxSize(),
        ) { items ->
            if (items != null) {
                val itemSpacer: LazyListScope.() -> Unit = {
                    item { Spacer(Modifier.height(Padding.Medium)) }
                }

                // There is a bug that if an item is empty, the padding is still present
                // But I don't want to waste my time fixing this, so sorry
                val contactAndMessage: LazyListScope.() -> Unit = {
                    item("messages") {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.animateItem(),
                        ) {
                            MessageList(
                                modifier = Modifier.fillMaxWidth(),
                                messages =
                                    listOfNotNull(
                                        items.header,
                                        items.footer,
                                    ).toImmutableList(),
                            )
                        }
                    }
                    itemSpacer()
                    item("openingHours") {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.animateItem(),
                        ) {
                            OpeningHoursList(
                                modifier = Modifier.fillMaxWidth(),
                                data = items.openingTimes,
                            )
                        }
                    }
                }
                val openingAndAddress: LazyListScope.() -> Unit = {
                    item("contacts") {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.animateItem(),
                        ) {
                            ContactList(
                                modifier = Modifier.fillMaxWidth(),
                                contactList = items.contacts,
                                onError = onError,
                            )
                        }
                    }
                    itemSpacer()
                    item("addresses") {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.animateItem(),
                        ) {
                            AddressList(
                                modifier = Modifier.fillMaxWidth(),
                                locations = listOfNotNull(items.address).toImmutableList(),
                                onError = onError,
                            )
                        }
                    }
                    itemSpacer()
                    item("links") {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.animateItem(),
                        ) {
                            LinkList(
                                modifier = Modifier.fillMaxWidth(),
                                links = items.links,
                            )
                        }
                    }
                }

                PullToRefreshWrapper(
                    isRefreshing = state.isLoading,
                    onRefresh = onRefresh,
                ) {
                    AboveOrSideBySideLayout(
                        topLeft = contactAndMessage,
                        bottomRight = openingAndAddress,
                        verticalSpacer = itemSpacer,
                    )
                }
            }
        }
    }
}
