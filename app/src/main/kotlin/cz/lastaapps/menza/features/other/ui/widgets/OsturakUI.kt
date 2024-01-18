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

package cz.lastaapps.menza.features.other.ui.widgets

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import cz.lastaapps.menza.R

@Composable
fun OsturakText(modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(R.string.osturak_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(stringResource(R.string.osturak_text))
        Text(
            stringResource(R.string.osturak_brought_by),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun OsturakImages(modifier: Modifier = Modifier) {

    val items = listOf(
        R.drawable.bodla to "bodla.cz",
        R.drawable.bohynekuchyne to "bohynekuchyne.cz",
        R.drawable.sachyvlasim to "sachyvlasim.cz",
    )

    BoxWithConstraints(modifier) {
        val width = min(maxWidth, 256.dp)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items.forEach {
                ImageWithImage(
                    imageId = it.first,
                    link = it.second,
                    modifier = Modifier.width(width)
                )
            }
        }
    }
}

@Composable
private fun ImageWithImage(
    @DrawableRes imageId: Int,
    link: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(painterResource(id = imageId), link, modifier = Modifier.fillMaxWidth())
        Text(
            "${stringResource(R.string.osturak_source)}: $link",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
