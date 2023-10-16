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

package cz.lastaapps.menza.features.settings.ui.screens

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.other.ui.dialog.ReportDialog
import cz.lastaapps.menza.features.other.ui.dialog.sendReport
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType.Agata
import cz.lastaapps.menza.features.settings.domain.model.DarkMode
import cz.lastaapps.menza.features.settings.domain.model.InitialSelectionBehaviour
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.settings.ui.components.FullReloadDialog
import cz.lastaapps.menza.features.settings.ui.components.SettingsItem
import cz.lastaapps.menza.features.settings.ui.components.SettingsSwitch
import cz.lastaapps.menza.features.settings.ui.util.name
import cz.lastaapps.menza.ui.components.ChooseFromDialog
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.PreviewWrapper
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun SettingsScreen(
    appTheme: AppThemeType,
    darkMode: DarkMode,
    onChooseTheme: () -> Unit,
    priceType: PriceType,
    onDiscounterPrices: (PriceType) -> Unit,
    downloadOnMetered: Boolean,
    onDownloadOnMetered: (Boolean) -> Unit,
    initialMenzaBehaviour: InitialSelectionBehaviour,
    onInitialMenzaBehaviour: (InitialSelectionBehaviour) -> Unit,
    menzaList: ImmutableList<Menza>,
    selectedMenza: Menza?,
    onSelectedMenza: (Menza) -> Unit,
    showAbout: Boolean,
    onAboutClicked: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onFullRefresh: () -> Unit,
    onCrashesDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Padding.None),
    ) {

//        Text(
//            text = stringResource(id = R.string.settings_title),
//            style = MaterialTheme.typography.displaySmall,
//            modifier = Modifier.padding(horizontal = SettingsTokens.itemPadding),
//        )

        // App theme
        SettingsItem(
            title = stringResource(id = R.string.settings_theme_title),
            subtitle = buildString {
                append(appTheme.name())
                append(", ")
                append(darkMode.name())
            },
            onClick = onChooseTheme,
        )

        // Discounted prices
        SettingsSwitch(
            title = stringResource(id = R.string.settings_switch_price),
            subtitle = priceType.name(),
            isChecked = priceType == PriceType.Discounted,
            onChecked = { onDiscounterPrices(priceType.other()) },
        )

        // Metered networks
        SettingsSwitch(
            title = stringResource(id = R.string.settings_switch_metered_title),
            subtitle = stringResource(id = R.string.settings_switch_metered_subtitle),
            isChecked = downloadOnMetered,
            onChecked = onDownloadOnMetered,
        )

        // Behaviour at startup
        InitialBehaviourSelector(
            initialMenzaBehaviour = initialMenzaBehaviour,
            onInitialMenzaBehaviour = onInitialMenzaBehaviour,
            menzaList = menzaList,
            selectedMenza = selectedMenza,
            onSelectedMenza = onSelectedMenza,
        )

        // About
        if (showAbout) {
            SettingsItem(
                title = stringResource(id = R.string.settings_about_title),
                subtitle = stringResource(id = R.string.settings_about_subtitle),
                onClick = onAboutClicked,
            )
        }

        Spacer(Modifier.height(Padding.Medium))

        Buttons(
            onPrivacyPolicy = onPrivacyPolicy,
            onFullRefresh = onFullRefresh,
            onCrashesDialog = onCrashesDialog,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
@Suppress("UnusedReceiverParameter")
private fun ColumnScope.InitialBehaviourSelector(
    initialMenzaBehaviour: InitialSelectionBehaviour,
    onInitialMenzaBehaviour: (InitialSelectionBehaviour) -> Unit,
    menzaList: ImmutableList<Menza>,
    selectedMenza: Menza?,
    onSelectedMenza: (Menza) -> Unit,
) {
    var initDialogVisible by remember { mutableStateOf(false) }

    // Behaviour
    SettingsItem(
        title = stringResource(id = R.string.settings_init_menza_title),
        subtitle = initialMenzaBehaviour.name(),
    ) { initDialogVisible = true }

    if (initDialogVisible) {

        val items = InitialSelectionBehaviour.entries
            .map { it to it.name() }
            .toImmutableList()

        ChooseFromDialog(
            title = stringResource(id = R.string.settings_init_menza_title),
            items = items,
            onItemSelected = { onInitialMenzaBehaviour(it.first) },
            onDismiss = { initDialogVisible = false },
            toString = Pair<InitialSelectionBehaviour, String>::second,
        )
    }


    // Specific menza
    var selectDialogVisible by remember { mutableStateOf(false) }

    if (initialMenzaBehaviour == InitialSelectionBehaviour.Specific) {
        SettingsItem(
            title = stringResource(id = R.string.settings_init_menza_select_title),
            subtitle = selectedMenza?.name
                ?: stringResource(id = R.string.settings_init_menza_select_placeholder),
        ) { selectDialogVisible = true }
    }

    if (selectDialogVisible) {

        ChooseFromDialog(
            title = stringResource(id = R.string.settings_init_menza_title),
            items = menzaList,
            onItemSelected = { onSelectedMenza(it) },
            onDismiss = { selectDialogVisible = false },
            toString = Menza::name,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Buttons(
    onPrivacyPolicy: () -> Unit,
    onFullRefresh: () -> Unit,
    onCrashesDialog: () -> Unit,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    uriHandler: UriHandler = LocalUriHandler.current,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            Padding.Small,
            Alignment.CenterHorizontally,
        ),
        verticalArrangement = Arrangement.Center,
    ) {

        val shareText = stringResource(R.string.settings_button_share_text)
        OutlinedButton(
            onClick = {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, null))
            },
        ) { IconAndText(Icons.Default.Share, R.string.settings_button_share) }

        OutlinedButton(
            onClick = { uriHandler.openUri("https://play.google.com/store/apps/details?id=cz.lastaapps.menza") },
        ) { IconAndText(Icons.Default.Star, R.string.settings_button_rate) }

        OutlinedButton(
            onClick = onPrivacyPolicy,
        ) { IconAndText(Icons.Default.Security, R.string.settings_button_privacy_policy) }

        ReportButton()

        CrashesButton(
            onCrashesDialog = onCrashesDialog,
        )

        FullDataReload(
            onFullRefresh = onFullRefresh,
        )
    }
}

@Composable
private fun FullDataReload(
    onFullRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showFullReload by rememberSaveable { mutableStateOf(false) }
    if (showFullReload) {
        FullReloadDialog(
            onDismissRequest = { showFullReload = false },
            onConfirm = onFullRefresh
        )
    }

    OutlinedButton(onClick = { showFullReload = true }, modifier) {
        IconAndText(Icons.Default.Refresh, R.string.settings_button_reload)
    }
}

@Composable
private fun ReportButton(modifier: Modifier = Modifier) {
    var shown by rememberSaveable { mutableStateOf(false) }

    OutlinedButton(onClick = { shown = true }, modifier) {
        IconAndText(Icons.Default.BugReport, R.string.settings_button_report)
    }

    val context = LocalContext.current
    ReportDialog(shown, { shown = false }) {
        sendReport(context, it)
        shown = false
    }
}

@Composable
fun CrashesButton(
    onCrashesDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(onClick = onCrashesDialog, modifier) {
        IconAndText(
            Icons.AutoMirrored.Default.ReceiptLong,
            stringResource(R.string.settings_button_crashes),
        )
    }
}

@Composable
private fun IconAndText(icon: ImageVector, @StringRes textId: Int) =
    IconAndText(icon, stringResource(textId))

@Composable
private fun IconAndText(icon: ImageVector, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null)
        Text(text, textAlign = TextAlign.Center)
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() = PreviewWrapper {
    SettingsScreen(
        appTheme = Agata,
        darkMode = DarkMode.System,
        onChooseTheme = {},
        priceType = PriceType.Discounted,
        onDiscounterPrices = {},
        downloadOnMetered = false,
        onDownloadOnMetered = {},
        initialMenzaBehaviour = InitialSelectionBehaviour.Specific,
        onInitialMenzaBehaviour = {},
        menzaList = persistentListOf(),
        selectedMenza = null,
        onSelectedMenza = {},
        showAbout = true,
        onAboutClicked = {},
        onFullRefresh = {},
        onPrivacyPolicy = {},
        onCrashesDialog = {},
    )
}
