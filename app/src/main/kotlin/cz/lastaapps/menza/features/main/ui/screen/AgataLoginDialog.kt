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

package cz.lastaapps.menza.features.main.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.outlined.Architecture
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.res.stringResource
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
import cz.lastaapps.menza.ui.util.withAutofill
import kotlinx.collections.immutable.persistentListOf

/**
 * @author Marekkon5 (base implementation, rewritten by me)
 */
@Composable
internal fun AgataLoginDialog(
    viewModel: AgataWalletLoginViewModel,
    onDismissRequest: () -> Unit,
) {
    val state by viewModel.flowState

    LaunchedEffect(key1 = state.loginDone) {
        if (state.loginDone) {
            viewModel.dismissLoginDone()
            onDismissRequest()
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
    onLogin: (BalanceAccountType) -> Unit,
) {
    BaseDialog(onDismissRequest = onDismissRequest) {
        var indexSelected by rememberSaveable { mutableIntStateOf(0) }

        AgataLoginDialogContent(
            indexSelected = indexSelected,
            onIndexSelected = { indexSelected = it },
            username = username,
            password = password,
            onUsername = onUsername,
            onPassword = onPassword,
            onDismissRequest = onDismissRequest,
            isLoading = isLoading,
            loginEnabled = loginEnabled,
            error = error,
            onLogin = onLogin,
        )
    }
}

@Composable
private fun AgataLoginDialogContent(
    indexSelected: Int,
    onIndexSelected: (Int) -> Unit,
    username: String,
    password: String,
    onUsername: (String) -> Unit,
    onPassword: (String) -> Unit,
    onDismissRequest: () -> Unit,
    isLoading: Boolean,
    loginEnabled: Boolean,
    onLogin: (BalanceAccountType) -> Unit,
    error: DomainError?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Padding.Small),
    ) {
        Text(
            text = stringResource(R.string.wallet_login_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
        )

        BalanceTypesTabs(
            indexSelected = indexSelected,
            onIndexSelected = onIndexSelected,
        )

        SubtitleWidget(
            indexSelected = indexSelected,
            modifier = Modifier
                .padding(vertical = Padding.Tiny)
                .animateContentSize(),
        )

        val modeType = when (indexSelected) {
            0 -> BalanceAccountType.Stravnik
            1 -> BalanceAccountType.CTU
            2 -> null
            else -> error("Mode index out of range: $indexSelected")
        }
        Box(Modifier.animateContentSize()) {
            modeType?.let {
                LoginForm(
                    balanceType = modeType,
                    username = username,
                    password = password,
                    onUsername = onUsername,
                    onPassword = onPassword,
                    onDismissRequest = onDismissRequest,
                    isLoading = isLoading,
                    loginEnabled = loginEnabled,
                    onLogin = onLogin,
                    error = error,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun BalanceTypesTabs(
    indexSelected: Int,
    onIndexSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    PrimaryTabRow(
        selectedTabIndex = indexSelected,
        modifier = modifier,
    ) {
        Tab(
            selected = indexSelected == 0,
            onClick = { onIndexSelected(0) },
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
            selected = indexSelected == 1,
            onClick = { onIndexSelected(1) },
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
            selected = indexSelected == 2,
            onClick = { onIndexSelected(2) },
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
    val subtitleText = when (indexSelected) {
        0 -> stringResource(R.string.wallet_login_subtitle_stravnik)
        1 -> stringResource(R.string.wallet_login_subtitle_ctu)
        2 -> stringResource(R.string.wallet_login_subtitle_uct)
        else -> error("Mode index out of range: $indexSelected")
    }
    Text(
        text = subtitleText,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LoginForm(
    balanceType: BalanceAccountType,
    username: String,
    password: String,
    onUsername: (String) -> Unit,
    onPassword: (String) -> Unit,
    onDismissRequest: () -> Unit,
    isLoading: Boolean,
    loginEnabled: Boolean,
    onLogin: (BalanceAccountType) -> Unit,
    error: DomainError?,
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(Padding.Small),
) {
    OutlinedTextField(
        modifier = Modifier.withAutofill(
            autofillTypes = persistentListOf(AutofillType.Username),
            onFill = onUsername,
        ),
        enabled = !isLoading,
        value = username,
        onValueChange = onUsername,
        label = { Text(stringResource(R.string.wallet_login_username)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Next,
        ),
    )

    var showPasswordInfo by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier.withAutofill(
            autofillTypes = persistentListOf(AutofillType.Password),
            onFill = onPassword,
        ),
        enabled = !isLoading,
        value = password,
        onValueChange = onPassword,
        label = { Text(stringResource(R.string.wallet_login_password)) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardActions = KeyboardActions {
            if (loginEnabled) {
                onLogin(balanceType)
            }
        },
        keyboardOptions = KeyboardOptions(
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

    error?.let {
        Card(
            colors = appCardColors(MaterialTheme.colorScheme.errorContainer),
        ) {
            Text(
                text = error.text(),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(Padding.Medium),
            )
        }
    }

    AnimatedVisibility(showPasswordInfo) {
        Text(
            text = stringResource(id = R.string.wallet_login_password_policy),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }

    Crossfade(targetState = isLoading, label = "isLoading_login_switch") { isLoading ->
        if (isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Padding.Small, Alignment.End),
            ) {
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
}

@Preview
@Composable
private fun AgataLoginDialogPreview() = PreviewWrapper {
    AgataLoginDialogContent(
        indexSelected = 0,
        onIndexSelected = {},
        username = "Sultán",
        password = "Solimán",
        onUsername = {},
        onPassword = {},
        onDismissRequest = {},
        isLoading = false,
        loginEnabled = true,
        onLogin = { },
        error = WalletError.InvalidCredentials,
    )
}

@Preview
@Composable
private fun AgataLoginDialogNotSupportedPreview() = PreviewWrapper {
    AgataLoginDialogContent(
        indexSelected = 2,
        onIndexSelected = {},
        username = "Sultán",
        password = "Solimán",
        onUsername = {},
        onPassword = {},
        onDismissRequest = {},
        isLoading = false,
        loginEnabled = true,
        onLogin = { },
        error = WalletError.InvalidCredentials,
    )
}
