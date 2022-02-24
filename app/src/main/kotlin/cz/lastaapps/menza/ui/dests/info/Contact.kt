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

package cz.lastaapps.menza.ui.dests.info

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cz.lastaapps.entity.info.Contact
import cz.lastaapps.entity.info.Email
import cz.lastaapps.entity.info.PhoneNumber
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.LocalSnackbarProvider
import kotlinx.coroutines.launch

@Composable
fun ContactList(
    contact: List<Contact>,
    modifier: Modifier = Modifier,
) {
    if (contact.isNotEmpty()) {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.info_contacts_title),
                style = MaterialTheme.typography.titleLarge
            )
            contact.forEach {
                ContactUI(contact = it, Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUI(
    contact: Contact,
    modifier: Modifier = Modifier,
) {
    if (contact.name == null && contact.role == null)
        return

    Card(
        modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        val context = LocalContext.current
        val snackbar = LocalSnackbarProvider.current
        val scope = rememberCoroutineScope()

        Column(Modifier.padding(12.dp)) {
            contact.name?.let {
                Text(text = it.name, style = MaterialTheme.typography.titleMedium)
            }
            contact.role?.let {
                Text(text = it.role, style = MaterialTheme.typography.titleMedium)
            }
            contact.phoneNumber?.let {
                val errorMessage = stringResource(R.string.info_contacts_dial_no_app)
                OutlinedButton(
                    onClick = {
                        makePhoneCall(context, it) {
                            scope.launch { snackbar.showSnackbar(errorMessage) }
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LocalContentColor.current),
                    border = BorderStroke(1.dp, LocalContentColor.current),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = it.phone,
                        style = LocalTextStyle.current.copy(
                            textDecoration = TextDecoration.Underline
                        ),
                    )
                }
            }
            contact.email?.let {
                val errorMessage = stringResource(R.string.info_contacts_email_no_app)
                OutlinedButton(
                    onClick = {
                        sendEmail(context, it) {
                            scope.launch { snackbar.showSnackbar(errorMessage) }
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LocalContentColor.current),
                    border = BorderStroke(1.dp, LocalContentColor.current),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = it.mail,
                        style = LocalTextStyle.current.copy(
                            textDecoration = TextDecoration.Underline
                        ),
                    )
                }
            }
            if (contact.phoneNumber ?: contact.email != null) {
                val errorMessage = stringResource(R.string.info_contacts_contact_no_app)
                OutlinedButton(
                    onClick = {
                        addContact(context, contact) {
                            scope.launch { snackbar.showSnackbar(errorMessage) }
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LocalContentColor.current),
                    border = BorderStroke(1.dp, LocalContentColor.current),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.info_contacts_contact_button_add))
                }
            }
        }
    }
}

private fun makePhoneCall(context: Context, phoneNumber: PhoneNumber, onError: () -> Unit) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:${Uri.encode(phoneNumber.phone)}")
    try {
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.info_contacts_dial_choose_app)
            )
        )
    } catch (e: Exception) {
        e.printStackTrace()
        onError()
    }
}

private fun sendEmail(context: Context, email: Email, onError: () -> Unit) {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:${Uri.encode(email.mail)}")
    try {
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.info_contacts_email_choose_app)
            )
        )
    } catch (e: Exception) {
        e.printStackTrace()
        onError()
    }
}

private fun addContact(context: Context, contact: Contact, onError: () -> Unit) {

    val intent = Intent(Intent.ACTION_INSERT).apply {
        type = ContactsContract.Contacts.CONTENT_TYPE

        val name = contact.name?.name ?: contact.role?.role!!
        putExtra(ContactsContract.Intents.Insert.NAME, name)

        contact.role?.let {
            putExtra(ContactsContract.Intents.Insert.JOB_TITLE, it.role)
        }
        contact.phoneNumber?.let {
            putExtra(ContactsContract.Intents.Insert.PHONE, it.phone)
        }
        contact.email?.let {
            putExtra(ContactsContract.Intents.Insert.EMAIL, it.mail)
        }
    }
    try {
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.info_contacts_contact_choose_app)
            )
        )
    } catch (e: Exception) {
        e.printStackTrace()
        onError()
    }
}
