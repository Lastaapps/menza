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

package cz.lastaapps.menza.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cz.lastaapps.menza.ui.theme.Padding.More

@Composable
fun BaseDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    scrollState: ScrollState? = rememberScrollState(), // use null to disable
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        // TODO remove when fixed
        // If a predictive back gesture is started, the dialog is dismissed right away,
        // because uses clicks outside
        properties =
            DialogProperties(
                dismissOnBackPress = properties.dismissOnBackPress,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = properties.usePlatformDefaultWidth,
            ),
    ) {
        val sourceBackground = remember { MutableInteractionSource() }
        val sourceSurface = remember { MutableInteractionSource() }

        Box(
            modifier =
                Modifier
                    .fillMaxSize(.95f)
                    .clickable(sourceBackground, indication = null) {
                        if (properties.dismissOnClickOutside) {
                            onDismissRequest()
                        }
                    },
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.large,
                modifier =
                    Modifier
                        .clickable(sourceSurface, indication = null) {},
            ) {
                Box(
                    modifier =
                        Modifier
                            .padding(More.Dialog)
                            .then(scrollState?.let { Modifier.verticalScroll(it) } ?: Modifier),
                ) {
                    content()
                }
            }
        }
    }
}
