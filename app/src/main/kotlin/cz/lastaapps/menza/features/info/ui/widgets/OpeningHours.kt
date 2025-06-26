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

package cz.lastaapps.menza.features.info.ui.widgets

import android.os.Build
import android.text.format.DateFormat
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cz.lastaapps.api.core.domain.model.PlaceOpeningInfo
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.theme.Padding
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.toJavaDayOfWeek
import kotlinx.datetime.toJavaLocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun OpeningHoursList(
    data: ImmutableList<PlaceOpeningInfo>,
    modifier: Modifier = Modifier,
) {
    if (data.isNotEmpty()) {
        Column(
            modifier = modifier.width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.info_opening_hours_title),
                style = MaterialTheme.typography.titleLarge,
            )
            data.forEach {
                OpeningHoursLocationUI(
                    data = it,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun OpeningHoursLocationUI(
    data: PlaceOpeningInfo,
    modifier: Modifier = Modifier,
    locale: Locale =
        LocalConfiguration.current.let {
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) it.locales[0] else it.locale
        },
    use24: Boolean = DateFormat.is24HourFormat(LocalContext.current),
) {
    val formatter =
        remember(use24) {
            val patter = if (use24) "H:mm" else "h:mm a"
            DateTimeFormatter.ofPattern(patter)
        }

    ElevatedCard(
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .padding(Padding.MidSmall)
                    .align(Alignment.CenterHorizontally),
        ) {
            Text(
                text = data.name,
                style = MaterialTheme.typography.titleMedium,
            )

            // types
            Column(
                modifier =
                    Modifier
                        .horizontalScroll(rememberScrollState())
                        .width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.spacedBy(Padding.Small),
            ) {
                data.types.forEachIndexed { typeIndex, type ->
                    if (typeIndex != 0) {
                        HorizontalDivider(color = LocalContentColor.current)
                    }

                    // times and type name
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Padding.Smaller),
                            modifier = Modifier.width(IntrinsicSize.Max),
                        ) {
                            type.times.forEach { time ->
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    val startDate =
                                        time.startDay
                                            .toJavaDayOfWeek()
                                            .getDisplayName(TextStyle.SHORT, locale)
                                    if (time.startDay != time.endDay) {
                                        val end =
                                            time.endDay
                                                .toJavaDayOfWeek()
                                                .getDisplayName(TextStyle.SHORT, locale)
                                        Text("$startDate - $end")
                                    } else {
                                        Text(startDate)
                                    }

                                    Spacer(Modifier.width(Padding.Medium))

                                    val startTime =
                                        time.startTime.toJavaLocalTime().format(formatter)
                                    val endTime = time.endTime.toJavaLocalTime().format(formatter)
                                    Text("$startTime - $endTime")
                                }
                            }
                        }

                        type.description?.let {
                            Spacer(Modifier.width(Padding.Medium))
                            Text(text = it)
                        }
                    }
                }
            }
        }
    }
}
