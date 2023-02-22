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

package cz.lastaapps.menza.features.info.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.lastaapps.core.domain.error.MenzaError
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.features.info.ui.components.AddressList
import cz.lastaapps.menza.features.info.ui.components.ContactList
import cz.lastaapps.menza.features.info.ui.components.LinkList
import cz.lastaapps.menza.features.info.ui.components.MessageList
import cz.lastaapps.menza.features.info.ui.components.OpeningHoursList
import cz.lastaapps.menza.features.info.ui.vm.InfoState
import cz.lastaapps.menza.features.info.ui.vm.InfoViewModel
import cz.lastaapps.menza.features.main.ui.components.WrapMenzaNotSelected
import cz.lastaapps.menza.ui.HandleError
import cz.lastaapps.menza.ui.components.WrapRefresh
import cz.lastaapps.menza.ui.components.layout.AboveOrSideBySideLayout
import cz.lastaapps.menza.ui.theme.MenzaPadding
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun InfoScreen(
    onOsturak: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InfoViewModel = koinViewModel(),
    hostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    var error by remember { mutableStateOf<MenzaError?>(null) }
    HandleError(error, hostState) { error = null }

    InfoEffects(viewModel, hostState)

    val state by viewModel.flowState
    InfoContent(
        state = state,
        onRefresh = viewModel::reload,
        onOsturak = onOsturak,
        onError = { error = it },
        modifier = modifier,
        hostState = hostState,
    )
}

@Composable
private fun InfoEffects(
    viewModel: InfoViewModel,
    hostState: SnackbarHostState,
) {
    HandleAppear(viewModel)
    HandleError(viewModel, hostState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InfoContent(
    state: InfoState,
    onRefresh: () -> Unit,
    onOsturak: () -> Unit,
    onError: (MenzaError) -> Unit,
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState) },
        modifier = modifier,
    ) { padding ->
        WrapMenzaNotSelected(
            menza = state.selectedMenza, onOsturak = onOsturak,
            modifier = Modifier.padding(padding),
        ) {
            Crossfade(
                targetState = state.items,
            ) { items ->
                if (items != null) {
                    val itemSpacer: LazyListScope.() -> Unit = {
                        item { Spacer(Modifier.height(MenzaPadding.Medium)) }
                    }

                    val contactAndMessage: LazyListScope.() -> Unit = {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                MessageList(
                                    messages = listOfNotNull(
                                        items.header, items.footer
                                    ).toImmutableList(),
                                )
                            }
                        }
                        itemSpacer()
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                OpeningHoursList(
                                    data = items.openingTimes,
                                )
                            }
                        }
                    }
                    val openingAndAddress: LazyListScope.() -> Unit = {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                ContactList(
                                    contactList = items.contacts,
                                    onError = onError,
                                )
                            }
                        }
                        itemSpacer()
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                AddressList(
                                    locations = listOfNotNull(items.address).toImmutableList(),
                                    onError = onError,
                                )
                            }
                        }
                        itemSpacer()
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                LinkList(
                                    links = items.links,
                                )
                            }
                        }
                    }

                    WrapRefresh(
                        refreshing = state.isLoading,
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
}
