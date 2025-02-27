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

package cz.lastaapps.menza.features.settings.ui.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cz.lastaapps.common.Communication
import cz.lastaapps.common.DeveloperInfo
import cz.lastaapps.common.R
import cz.lastaapps.common.R.drawable
import cz.lastaapps.menza.BuildConfig
import cz.lastaapps.menza.ui.theme.Padding
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun AboutScreen(
    onOsturak: () -> Unit,
    onLicense: () -> Unit,
    onShowWhatsNew: () -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    Box(modifier, contentAlignment = Alignment.TopCenter) {
        Column(
            modifier =
                Modifier
                    .verticalScroll(scrollState)
                    .width(IntrinsicSize.Max),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
        ) {
            Surface(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.padding(Padding.Medium),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(cz.lastaapps.menza.R.string.about_title),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            ElevatedCard {
                Column(
                    modifier =
                        Modifier
                            .padding(Padding.MidSmall)
                            .align(Alignment.CenterHorizontally),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Padding.Small),
                ) {
                    DataSource(modifier = Modifier.fillMaxWidth())
                }
            }
            ElevatedCard {
                Column(
                    Modifier
                        .padding(12.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        stringResource(cz.lastaapps.menza.R.string.about_find_out_more),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                    MenzaCliButton(
                        modifier = Modifier.fillMaxWidth(),
                    )
                    WhatsNewButton(
                        onShowWhatsNew = onShowWhatsNew,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    LicenseButton(onLicense, Modifier.fillMaxWidth())
                    OsturakButton(onOsturak, Modifier.fillMaxWidth())
                    ViewSource(Modifier.fillMaxWidth())
                }
            }
            ElevatedCard(Modifier.fillMaxWidth()) {
                Socials(Modifier.padding(12.dp))
            }
            ElevatedCard {
                Contributors(
                    modifier =
                        Modifier
                            .padding(12.dp)
                            .align(Alignment.CenterHorizontally),
                )
            }
            Surface(Modifier.fillMaxWidth()) {
                AppInfo(Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
private fun DataSource(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(cz.lastaapps.menza.R.string.about_data_source),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        DatasourceButton(
            title = "agata.suz.cvut.cz",
            url = "https://agata.suz.cvut.cz/",
        )
        DatasourceButton(
            title = "studentcatering.cz",
            url = "https://studentcatering.cz/jidelni-listek/",
        )
    }
}

@Composable
private fun DatasourceButton(
    title: String,
    url: String,
    modifier: Modifier = Modifier,
    uriHandler: UriHandler = LocalUriHandler.current,
) {
    OutlinedButton(
        modifier = modifier.fillMaxWidth(),
        onClick = { uriHandler.openUri(url) },
    ) {
        IconAndText(
            {
                Icon(Icons.Default.Language, null)
            },
            {
                Text(
                    text = title,
                    style = LocalTextStyle.current.copy(textDecoration = TextDecoration.Underline),
                    textAlign = TextAlign.Center,
                )
            },
        )
    }
}

@Composable
private fun MenzaCliButton(
    modifier: Modifier = Modifier,
    handler: UriHandler = LocalUriHandler.current,
) {
    OutlinedButton(
        modifier = modifier,
        onClick = { handler.openUri("https://github.com/Lastaapps/menza-cli") },
    ) {
        IconAndText(
            icon = {
                Icon(Icons.Default.Terminal, null)
            },
            text = {
                Text(
                    text = stringResource(id = cz.lastaapps.menza.R.string.about_menza_cli),
                    textAlign = TextAlign.Center,
                )
            },
        )
    }
}

@Composable
private fun WhatsNewButton(
    onShowWhatsNew: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onShowWhatsNew,
    ) {
        IconAndText(
            icon = {
                Icon(Icons.Default.NewReleases, null)
            },
            text = {
                Text(
                    text = stringResource(id = cz.lastaapps.menza.R.string.about_whats_new),
                    textAlign = TextAlign.Center,
                )
            },
        )
    }
}

@Composable
private fun LicenseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        IconAndText(
            {
                Icon(Icons.Default.Description, null)
            },
            {
                Text(
                    text = stringResource(cz.lastaapps.menza.R.string.about_license_notices),
                    textAlign = TextAlign.Center,
                )
            },
        )
    }
}

@Composable
private fun OsturakButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        IconAndText(
            {
                Icon(Icons.Default.LocalFireDepartment, null)
            },
            {
                Text(
                    text = stringResource(cz.lastaapps.menza.R.string.about_osturak),
                    textAlign = TextAlign.Center,
                )
            },
        )
    }
}

@Composable
private fun ViewSource(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    OutlinedButton(
        modifier = modifier,
        onClick = { Communication.openProjectsGithub(context, "Menza") },
    ) {
        IconAndText(
            icon = {
                Icon(
                    Icons.Default.Code,
                    stringResource(R.string.content_description_github_project),
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.github_project),
                    textAlign = TextAlign.Center,
                )
            },
        )
    }
}

@Composable
private fun IconAndText(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
    ) {
        icon()
        text()
    }
}

@Composable
private fun Contributor(
    name: String,
    url: String,
    modifier: Modifier = Modifier,
    uriHandler: UriHandler = LocalUriHandler.current,
) {
    OutlinedButton(
        modifier = modifier,
        onClick = { uriHandler.openUri(url) },
    ) {
        Text(text = name)
    }
}

@Composable
private fun Socials(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val icons =
        listOf(
            SocialItem(
                drawable.ic_matrix,
                R.string.content_description_matrix,
            ) { Communication.openMatrix(context) },
            SocialItem(
                drawable.ic_telegram,
                R.string.content_description_telegram,
            ) { Communication.openTelegram(context) },
            SocialItem(
                drawable.ic_github,
                R.string.content_description_github,
            ) { Communication.openGithub(context) },
            SocialItem(
                drawable.ic_discord,
                R.string.content_description_discord,
            ) { Communication.openDiscord(context) },
            SocialItem(
                drawable.ic_facebook,
                R.string.content_description_facebook,
            ) { Communication.openFacebook(context) },
            SocialItem(
                drawable.ic_play_store,
                R.string.content_description_play_store,
            ) { Communication.openPlayStore(context) },
        )

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(cz.lastaapps.menza.R.string.about_developer_contact),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            icons.forEach {
                IconButton(onClick = it.onClick) {
                    Image(
                        painterResource(id = it.drawableId),
                        contentDescription = stringResource(id = it.contentDescriptionId),
                    )
                }
            }
        }
    }
}

private data class SocialItem(
    @DrawableRes val drawableId: Int,
    @StringRes val contentDescriptionId: Int,
    val onClick: () -> Unit,
)

@Composable
private fun Contributors(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            stringResource(cz.lastaapps.menza.R.string.about_contributors),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )

        remember {
            persistentListOf(
                "LastaApps" to "https://github.com/Lastaapps",
                "Marekkon5" to "https://github.com/Marekkon5",
            )
        }.forEach { (user, url) ->
            Contributor(
                user,
                url,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun AppInfo(modifier: Modifier = Modifier) {
    val developer = DeveloperInfo.getNameAndBuildYear(LocalContext.current)
    val appInfo = "${BuildConfig.VERSION_NAME} - ${BuildConfig.VERSION_CODE}"
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Text(developer, textAlign = TextAlign.Center)
        Text(appInfo, textAlign = TextAlign.Center)
    }
}
