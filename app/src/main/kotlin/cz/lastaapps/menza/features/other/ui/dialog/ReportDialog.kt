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

package cz.lastaapps.menza.features.other.ui.dialog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cz.lastaapps.common.Communication
import cz.lastaapps.common.R
import cz.lastaapps.crash.entity.Crash
import cz.lastaapps.crash.entity.ErrorSeverity
import cz.lastaapps.menza.BuildConfig
import cz.lastaapps.menza.features.other.ui.dialog.ReportMode.Clipboard
import cz.lastaapps.menza.features.other.ui.dialog.ReportMode.Discord
import cz.lastaapps.menza.features.other.ui.dialog.ReportMode.Email
import cz.lastaapps.menza.features.other.ui.dialog.ReportMode.Facebook
import cz.lastaapps.menza.features.other.ui.dialog.ReportMode.GitHub
import cz.lastaapps.menza.features.other.ui.dialog.ReportMode.Matrix
import cz.lastaapps.menza.features.other.ui.dialog.ReportMode.Telegram
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

sealed class ReportMode {
    data object Matrix : ReportMode()

    data object Telegram : ReportMode()

    data object Discord : ReportMode()

    data object GitHub : ReportMode()

    data object Facebook : ReportMode()

    data object Email : ReportMode()

    data object Clipboard : ReportMode()
}

@Composable
fun ReportDialog(
    shown: Boolean,
    reportsCrash: Boolean,
    onDismissRequest: () -> Unit,
    onMode: (ReportMode) -> Unit,
) {
    if (shown) {
        ReportDialog(
            reportsCrash,
            onDismissRequest,
            onMode,
        )
    }
}

@Composable
private fun ReportDialog(
    reportsCrash: Boolean,
    onDismissRequest: () -> Unit,
    onMode: (ReportMode) -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.extraLarge) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .width(IntrinsicSize.Max)
                        .padding(16.dp),
            ) {
                Text(
                    stringResource(cz.lastaapps.menza.R.string.report_title),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                Button(
                    onClick = { onMode(GitHub) },
                    Modifier.fillMaxWidth(),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painterResource(id = R.drawable.ic_github),
                            null,
                            Modifier.size(24.dp),
                        )
                        Text(stringResource(cz.lastaapps.menza.R.string.report_github))
                    }
                }
                Button(
                    onClick = { onMode(Matrix) },
                    Modifier.fillMaxWidth(),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painterResource(R.drawable.ic_matrix),
                            null,
                            Modifier.size(24.dp),
                        )
                        Text(stringResource(cz.lastaapps.menza.R.string.report_matrix))
                    }
                }
                Button(
                    onClick = { onMode(Telegram) },
                    Modifier.fillMaxWidth(),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painterResource(R.drawable.ic_telegram),
                            null,
                            Modifier.size(24.dp),
                        )
                        Text(stringResource(cz.lastaapps.menza.R.string.report_telegram))
                    }
                }
                Button(
                    onClick = { onMode(Discord) },
                    Modifier.fillMaxWidth(),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painterResource(R.drawable.ic_discord),
                            null,
                            Modifier.size(24.dp),
                        )
                        Text(stringResource(cz.lastaapps.menza.R.string.report_discord))
                    }
                }
                // Fuck Mark
//                Button(
//                    onClick = { onModeSelected(ReportMode.Facebook) },
//                    Modifier.fillMaxWidth()
//                ) {
//                    Row(
//                        horizontalArrangement = Arrangement.spacedBy(8.dp),
//                        verticalAlignment = Alignment.CenterVertically,
//                    ) {
//                        Image(
//                            painterResource(R.drawable.ic_facebook),
//                            null,
//                            Modifier.size(24.dp)
//                        )
//                        Text(stringResource(cz.lastaapps.menza.R.string.report_facebook))
//                    }
//                }
                Button(
                    onClick = { onMode(Email) },
                    Modifier.fillMaxWidth(),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(Icons.Default.Email, null, Modifier.size(24.dp))
                        Text(stringResource(cz.lastaapps.menza.R.string.report_email))
                    }
                }
                if (reportsCrash) {
                    Button(
                        onClick = { onMode(Clipboard) },
                        Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Filled.ContentCopy, null, Modifier.size(24.dp))
                            Text(stringResource(cz.lastaapps.menza.R.string.report_clipboard))
                        }
                    }
                }
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(cz.lastaapps.menza.R.string.report_cancel))
                }
            }
        }
    }
}

fun sendReport(
    context: Context,
    mode: ReportMode,
    errorText: String,
    extraMessage: String?,
    throwable: Throwable?,
) {
    val text =
        """
        |${context.getString(cz.lastaapps.menza.R.string.report_add_description)}
        |
        |
        |${getPhoneInfo(context)}
        |
        |"Internal app problem"
        |${LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)}
        |${errorText}
        |
        |${extraMessage ?: ""}
        |
        |${throwable?.message ?: "Unknown error message"}
        |${throwable?.stackTraceToString() ?: ""}
        """.trimMargin()
    doSend(context, mode, text)
}

fun sendReport(
    context: Context,
    mode: ReportMode,
    crash: Crash,
) {
    val text =
        """
        |${context.getString(cz.lastaapps.menza.R.string.report_add_description)}
        |
        |
        |${getPhoneInfo(context)}
        |
        |${
            when (crash.severity) {
                ErrorSeverity.CRASH -> "App crashed"
                ErrorSeverity.HANDLED -> "Internal app problem"
            }
        }
        |${crash.date.format(DateTimeFormatter.ISO_DATE_TIME)}
        |${crash.message}
        |${crash.trace}
        """.trimMargin()
    doSend(context, mode, text)
}

private fun doSend(
    context: Context,
    mode: ReportMode,
    text: String,
) {
    copyToClipboard(context, text)

    when (mode) {
        Matrix -> sendMatrix(context, text)
        Telegram -> sendTelegram(context, text)
        GitHub -> sendGitHub(context, text)
        Discord -> sendDiscord(context, text)
        Facebook -> sendFacebook(context, text)
        Email -> sendEmail(context, text)
        Clipboard -> {}
    }
}

private fun getPhoneInfo(context: Context): String {
    val metrics = context.resources.displayMetrics
    return """
        |Android version:    ${Build.VERSION.SDK_INT}
        |App version name:   ${BuildConfig.VERSION_NAME}
        |App version code:   ${BuildConfig.VERSION_CODE}
        |Phone model:        ${Build.MODEL}
        |Phone manufacturer: ${Build.MANUFACTURER} 
        |Date and Time:      ${ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)}
        |Screen size:        ${metrics.widthPixels} x ${metrics.heightPixels} px
        """.trimMargin()
}

private fun copyToClipboard(
    context: Context,
    text: String,
) {
    val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    val clip =
        ClipData.newPlainText(
            context.getString(cz.lastaapps.menza.R.string.report_toast_clipboard_title),
            text,
        )
    clipboard.setPrimaryClip(clip)
    Toast
        .makeText(context, cz.lastaapps.menza.R.string.report_toast_clipboard, Toast.LENGTH_LONG)
        .show()
}

private fun sendMatrix(
    context: Context,
    @Suppress("UNUSED_PARAMETER") text: String,
) {
    val intent =
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://matrix.to/#/#lastaapps_menza:matrix.org"),
        )
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun sendTelegram(
    context: Context,
    text: String,
) {
    // val myId = Uri.encode("-1001494132666") //group id
    val groupName = Uri.encode("lastaapps")
    val encoded = Uri.encode(text)

    val intent =
        Intent(
            Intent.ACTION_VIEW,
            // Uri.parse("tg://msg?to=$myId&text=$encoded"),
            Uri.parse("tg://resolve?domain=$groupName&text=$encoded"),
        )
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        val webIntent =
            Intent(
                Intent.ACTION_VIEW,
                // Uri.parse("https://t.me/share/msg?to=$myId&text=$encoded"),
                Uri.parse("http://www.telegram.me/$groupName"),
            )
        context.startActivity(webIntent)
    }
}

private fun sendGitHub(
    context: Context,
    @Suppress("UNUSED_PARAMETER") text: String,
) {
    val intent =
        Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Lastaapps/Menza/issues/new"))
    context.startActivity(intent)
}

private fun sendDiscord(
    context: Context,
    @Suppress("UNUSED_PARAMETER") text: String,
) {
    Communication.openDiscord(context)
}

private fun sendFacebook(
    context: Context,
    @Suppress("UNUSED_PARAMETER") text: String,
) {
    Communication.openFacebook(context)
}

private fun sendEmail(
    context: Context,
    text: String,
) {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, "lastaappsdev@gmail.com")
    intent.putExtra(Intent.EXTRA_SUBJECT, "Menza problem report")
    intent.putExtra(Intent.EXTRA_TEXT, text)
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast
            .makeText(context, cz.lastaapps.menza.R.string.report_email_no_app, Toast.LENGTH_LONG)
            .show()
    }
}
