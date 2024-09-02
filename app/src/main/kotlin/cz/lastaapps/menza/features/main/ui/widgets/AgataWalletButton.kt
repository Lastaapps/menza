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

package cz.lastaapps.menza.features.main.ui.widgets

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import cz.lastaapps.api.core.domain.model.BalanceAccountType.CTU
import cz.lastaapps.api.core.domain.model.UserBalance
import cz.lastaapps.api.core.domain.model.formattedBalance
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.main.ui.vm.AgataWalletViewModel
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.HandleError
import cz.lastaapps.menza.ui.util.PreviewWrapper

@Composable
internal fun AgataWalletButton(
    viewModel: AgataWalletViewModel,
    snackbarHostState: SnackbarHostState,
    onShowLoginDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HandleAppear(viewModel)
    HandleError(viewModel, hostState = snackbarHostState)

    val state by viewModel.flowState

    AgataWalletButton(
        balance = state.balance,
        isLoading = state.isLoading,
        isWarning = state.isWarning,
        onShowLoginDialog = onShowLoginDialog,
        onReload = viewModel::refresh,
        onOpenWeb = viewModel::onOpenWeb,
        onLogout = viewModel::logout,
        modifier = modifier,
    )
}

@Composable
internal fun AgataWalletButton(
    balance: Option<UserBalance?>,
    isLoading: Boolean,
    isWarning: Boolean,
    onShowLoginDialog: () -> Unit,
    onReload: () -> Unit,
    onOpenWeb: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier.animateContentSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(Padding.Smaller),
) {
    Text(
        text = stringResource(id = R.string.wallet_description),
        style = MaterialTheme.typography.titleMedium,
    )

    when (balance) {
        None -> {}
        is Some -> {
            val value = balance.value
            if (value == null) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(Padding.More.Icon),
                    )
                } else {
                    Button(
                        onClick = onShowLoginDialog,
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text(text = stringResource(id = R.string.wallet_login))
                    }
                }
            } else {
                ButtonContent(
                    balance = value,
                    isLoading = isLoading,
                    isWarning = isWarning,
                    onReload = onReload,
                    onOpenWeb = onOpenWeb,
                    onLogout = onLogout,
                )
            }
        }
    }
}

@Suppress("UnusedReceiverParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.ButtonContent(
    balance: UserBalance,
    isLoading: Boolean,
    isWarning: Boolean,
    onReload: () -> Unit,
    onOpenWeb: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Balance || Loading || Warning
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Padding.Small),
    ) {
        Button(
            shape = MaterialTheme.shapes.medium,
            onClick = onReload,
        ) {
            Row(
                modifier =
                    Modifier
                        .animateContentSize(),
                horizontalArrangement = Arrangement.spacedBy(Padding.Small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = balance.formattedBalance(),
                    style = MaterialTheme.typography.labelLarge,
                    // fontWeight = FontWeight.Bold,
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(Padding.More.Icon),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        trackColor = MaterialTheme.colorScheme.primary,
                    )
                }
                if (isWarning) {
                    Icon(
                        Icons.Rounded.Warning,
                        contentDescription = stringResource(R.string.wallet_update_error),
                    )
                }
            }
        }

        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip {
                    Text(text = stringResource(id = R.string.wallet_web))
                }
            },
            state = rememberTooltipState(isPersistent = true),
        ) {
            IconButton(onClick = onOpenWeb) {
                Icon(
                    Icons.Outlined.Money,
                    contentDescription = stringResource(id = R.string.wallet_web),
                )
            }
        }

        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip {
                    Text(text = stringResource(id = R.string.wallet_logout))
                }
            },
            state = rememberTooltipState(isPersistent = true),
        ) {
            IconButton(onClick = onLogout) {
                Icon(
                    Icons.AutoMirrored.Default.Logout,
                    contentDescription = stringResource(id = R.string.wallet_logout),
                )
            }
        }
    }

    Text(
        text = stringResource(id = R.string.wallet_logged_in_as, balance.username),
        style = MaterialTheme.typography.bodySmall,
    )
}

@Preview
@Composable
private fun AgataWalletButtonLogInPreview() =
    PreviewWrapper {
        AgataWalletButton(
            balance = Some(null),
            isLoading = false,
            isWarning = false,
            onShowLoginDialog = {},
            onReload = {},
            onOpenWeb = {},
            onLogout = {},
        )
    }

@Preview
@Composable
private fun AgataWalletButtonPreview() =
    PreviewWrapper {
        AgataWalletButton(
            balance = Some(UserBalance("Jára", 420f, CTU)),
            isLoading = true,
            isWarning = true,
            onShowLoginDialog = {},
            onReload = {},
            onOpenWeb = {},
            onLogout = {},
        )
    }
