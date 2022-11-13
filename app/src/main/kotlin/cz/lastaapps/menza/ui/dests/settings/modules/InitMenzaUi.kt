/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.ui.dests.settings.modules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.lastaapps.entity.menza.Menza
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.dests.settings.SettingsViewModel
import cz.lastaapps.menza.ui.dests.settings.store.InitMenza
import cz.lastaapps.menza.ui.dests.settings.store.initMenza
import cz.lastaapps.menza.ui.dests.settings.store.preferredMenza
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun InitMenzaUI(
    menzaViewModel: MenzaViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    var initExpanded by rememberSaveable { mutableStateOf(false) }
    var preferredExpanded by rememberSaveable { mutableStateOf(false) }

    val mode by settingsViewModel.sett.initMenza.collectAsState()
    val menzaId by settingsViewModel.sett.preferredMenza.collectAsState()
    val menzaList by menzaViewModel.data.collectAsState()

    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InitMenzaRow(
            expanded = initExpanded,
            onExpanded = { initExpanded = it },
            mode = mode,
            onMode = { settingsViewModel.setInitMenza(it) },
            modifier = Modifier.fillMaxWidth()
        )

        if (mode == InitMenza.Specific) {
            PreferredMenza(
                expanded = preferredExpanded,
                onExpanded = { preferredExpanded = it },
                menza = menzaId,
                onMenza = { settingsViewModel.setPreferredMenza(it) },
                menzaList = menzaList,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InitMenzaRow(
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    mode: InitMenza,
    onMode: (InitMenza) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val options = remember(context) {
        persistentListOf(
            InitMenza.Ask to context.getString(R.string.settings_init_menza_ask),
            InitMenza.Remember to context.getString(R.string.settings_init_menza_remember),
            InitMenza.Specific to context.getString(R.string.settings_init_menza_specific),
        )
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpanded(!expanded) },
    ) {
        TextField(
            readOnly = true,
            value = options.first { it.first == mode }.second,
            onValueChange = { },
            label = { Text(stringResource(R.string.settings_init_menza_behaviour)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = modifier.menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpanded(false) }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(text = selectionOption.second) },
                    onClick = {
                        onMode(selectionOption.first)
                        onExpanded(false)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreferredMenza(
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    menza: MenzaId?,
    onMenza: (MenzaId) -> Unit,
    menzaList: ImmutableList<Menza>,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpanded(!expanded) },
    ) {
        TextField(
            readOnly = true,
            value = menzaList.firstOrNull { it.menzaId == menza }?.name
                ?: stringResource(R.string.settings_init_menza_select_placeholder),
            onValueChange = { },
            label = { Text(stringResource(R.string.settings_init_menza_select_title)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = modifier.menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpanded(false) }
        ) {
            menzaList.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(text = selectionOption.name) },
                    onClick = {
                        onMenza(selectionOption.menzaId)
                        onExpanded(false)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
