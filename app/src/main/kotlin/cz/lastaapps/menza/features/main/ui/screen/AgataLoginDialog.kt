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

package cz.lastaapps.menza.features.main.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.outlined.Architecture
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cz.lastaapps.api.core.domain.model.BalanceAccountType
import cz.lastaapps.core.domain.error.ApiError.WalletError
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.ui.text
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.main.ui.vm.AgataWalletLoginViewModel
import cz.lastaapps.menza.ui.components.BaseDialog
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.PreviewWrapper
import cz.lastaapps.menza.ui.util.appCardColors

/**
 * @author Marekkon5 (base implementation, rewritten by me)
 */
@Composable
internal fun AgataLoginDialog(
    viewModel: AgataWalletLoginViewModel,
    onDismissRequest: () -> Unit,
) {
    val state by viewModel.flowState
    val onDismissRequestLambda by rememberUpdatedState(onDismissRequest)

    LaunchedEffect(key1 = state.loginDone) {
        if (state.loginDone) {
            viewModel.dismissLoginDone()
            onDismissRequestLambda()
        }
    }

    AgataLoginDialog(
        username = state.username,
        password = state.password,
        onUsername = viewModel::setUsername,
        onPassword = viewModel::setPassword,
        onDismissRequest = {
            // resets internal viewmodel state
            viewModel.dismissLoginDone()
            onDismissRequest()
        },
        isLoading = state.isLoading,
        loginEnabled = state.enabled,
        onSetupRequest = viewModel::setup,
        onLogin = viewModel::logIn,
        error = state.error,
    )
}

@Composable
private fun AgataLoginDialog(
    username: String,
    password: String,
    onUsername: (String) -> Unit,
    onPassword: (String) -> Unit,
    onDismissRequest: () -> Unit,
    isLoading: Boolean,
    loginEnabled: Boolean,
    error: DomainError?,
    onSetupRequest: () -> Unit,
    onLogin: (BalanceAccountType) -> Unit,
) {
    BaseDialog(onDismissRequest = onDismissRequest) {
        var indexSelected by rememberSaveable { mutableIntStateOf(0) }

        AgataLoginDialogContent(
            selectedIndex = indexSelected,
            onSelectIndex = { indexSelected = it },
            username = username,
            password = password,
            onUsername = onUsername,
            onPassword = onPassword,
            onDismissRequest = onDismissRequest,
            isLoading = isLoading,
            loginEnabled = loginEnabled,
            error = error,
            onSetupRequest = onSetupRequest,
            onLogin = onLogin,
        )
    }
}

@Composable
private fun AgataLoginDialogContent(
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    username: String,
    password: String,
    onUsername: (String) -> Unit,
    onPassword: (String) -> Unit,
    onDismissRequest: () -> Unit,
    isLoading: Boolean,
    loginEnabled: Boolean,
    onSetupRequest: () -> Unit,
    onLogin: (BalanceAccountType) -> Unit,
    error: DomainError?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Padding.MidSmall),
    ) {
        Text(
            text = stringResource(R.string.wallet_login_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
        )

        if (false) {
            BalanceTypesTabs(
                selectedIndex = selectedIndex,
                onSelectIndex = onSelectIndex,
            )
        }

        val modeType: BalanceAccountType =
            when (selectedIndex) {
                0 -> BalanceAccountType.Stravnik
                else -> error("Mode index out of range: $selectedIndex")
            }

        TextFields(
            balanceType = modeType,
            username = username,
            password = password,
            onUsername = onUsername,
            onPassword = onPassword,
            isLoading = isLoading,
            loginEnabled = loginEnabled,
            onLogin = onLogin,
        )

        HorizontalDivider()

        SubtitleWidget(
            indexSelected = selectedIndex,
            modifier =
                Modifier
                    .padding(vertical = Padding.Tiny)
                    .animateContentSize(),
        )

        Error(error)

        ActionButtons(
            onDismissRequest = onDismissRequest,
            isLoading = isLoading,
            loginEnabled = loginEnabled,
            onLogin = onLogin,
            onSetupRequest = onSetupRequest,
            balanceType = modeType,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BalanceTypesTabs(
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    PrimaryTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier,
    ) {
        Tab(
            selected = selectedIndex == 0,
            onClick = { onSelectIndex(0) },
            text = {
                Text(
                    text = stringResource(id = R.string.wallet_login_tab_stravnik),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee(),
                )
            },
            icon = { Icon(Icons.Outlined.RestaurantMenu, null) },
        )
        Tab(
            selected = selectedIndex == 1,
            onClick = { onSelectIndex(1) },
            text = {
                Text(
                    text = stringResource(id = R.string.wallet_login_tab_ctu),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee(),
                )
            },
            icon = { Icon(Icons.Outlined.Architecture, null) },
        )
        Tab(
            selected = selectedIndex == 2,
            onClick = { onSelectIndex(2) },
            text = {
                Text(
                    text = stringResource(id = R.string.wallet_login_tab_uct),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee(),
                )
            },
            icon = { Icon(Icons.Outlined.Science, null) },
        )
    }
}

@Composable
private fun SubtitleWidget(
    indexSelected: Int,
    modifier: Modifier = Modifier,
) {
    val subtitleText =
        when (indexSelected) {
            0 -> stringResource(R.string.wallet_login_subtitle_stravnik)
            else -> error("Mode index out of range: $indexSelected")
        }
    Text(
        text = subtitleText,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier,
    )
}

@Composable
private fun TextFields(
    balanceType: BalanceAccountType,
    username: String,
    password: String,
    onUsername: (String) -> Unit,
    onPassword: (String) -> Unit,
    isLoading: Boolean,
    loginEnabled: Boolean,
    onLogin: (BalanceAccountType) -> Unit,
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(Padding.Tiny),
) {
    OutlinedTextField(
        modifier =
            Modifier.semantics {
                contentType = ContentType.Username
            },
        enabled = !isLoading,
        value = username,
        onValueChange = onUsername,
        label = { Text(stringResource(R.string.wallet_login_username)) },
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Next,
            ),
    )

    var showPasswordInfo by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier =
            Modifier.semantics {
                contentType = ContentType.Password
            },
        enabled = !isLoading,
        value = password,
        onValueChange = onPassword,
        label = { Text(stringResource(R.string.wallet_login_password)) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardActions =
            KeyboardActions {
                if (loginEnabled) {
                    onLogin(balanceType)
                }
            },
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Go,
            ),
        trailingIcon = {
            IconButton(onClick = { showPasswordInfo = !showPasswordInfo }) {
                Icon(
                    Icons.AutoMirrored.Default.HelpOutline,
                    contentDescription = stringResource(id = R.string.wallet_login_password_policy_hint),
                )
            }
        },
    )

    AnimatedVisibility(showPasswordInfo) {
        Text(
            text = stringResource(id = R.string.wallet_login_password_policy),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun Error(
    error: DomainError?,
    modifier: Modifier = Modifier,
) = Box(modifier = modifier.animateContentSize()) {
    if (error == null) return@Box

    Card(
        colors = appCardColors(MaterialTheme.colorScheme.errorContainer),
        modifier = modifier,
    ) {
        Text(
            text = error.text(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(Padding.Medium),
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ActionButtons(
    balanceType: BalanceAccountType,
    onDismissRequest: () -> Unit,
    isLoading: Boolean,
    loginEnabled: Boolean,
    onSetupRequest: () -> Unit,
    onLogin: (BalanceAccountType) -> Unit,
    modifier: Modifier = Modifier,
) = Crossfade(
    targetState = isLoading,
    label = "isLoading_login_switch",
    modifier = modifier,
) { isLoading ->
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Padding.Small, Alignment.CenterHorizontally),
    ) {
        if (isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.CenterVertically))
        } else {
            TooltipBox(
                positionProvider =
                    TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Above,
                    ),
                    tooltip = {
                    PlainTooltip {
                        Text(text = stringResource(R.string.wallet_login_setup))
                    }
                },
                state = rememberTooltipState(isPersistent = true),
            ) {
                IconButton(onClick = onSetupRequest) {
                    Icon(
                        Icons.Default.ManageAccounts,
                        contentDescription = stringResource(R.string.wallet_login_setup),
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            TextButton(
                onClick = { onDismissRequest() },
            ) {
                Text(stringResource(R.string.wallet_login_cancel))
            }

            Button(
                onClick = { onLogin(balanceType) },
                enabled = loginEnabled,
            ) {
                Text(stringResource(R.string.wallet_login_save))
            }
        }
    }
}

@Preview
@Composable
private fun AgataLoginDialogPreview() =
    PreviewWrapper {
        AgataLoginDialogContent(
            selectedIndex = 0,
            onSelectIndex = {},
            username = "Sultán",
            password = "Solimán",
            onUsername = {},
            onPassword = {},
            onDismissRequest = {},
            isLoading = false,
            loginEnabled = true,
            onSetupRequest = {},
            onLogin = { },
            error = WalletError.InvalidCredentials,
        )
    }

@Preview
@Composable
private fun AgataLoginDialogNotSupportedPreview() =
    PreviewWrapper {
        AgataLoginDialogContent(
            selectedIndex = 2,
            onSelectIndex = {},
            username = "Sultán",
            password = "Solimán",
            onUsername = {},
            onPassword = {},
            onDismissRequest = {},
            isLoading = false,
            loginEnabled = true,
            onSetupRequest = {},
            onLogin = { },
            error = WalletError.InvalidCredentials,
        )
    }
