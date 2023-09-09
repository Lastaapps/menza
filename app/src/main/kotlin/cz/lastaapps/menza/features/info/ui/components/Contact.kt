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

package cz.lastaapps.menza.features.info.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cz.lastaapps.api.core.domain.model.Contact
import cz.lastaapps.api.core.domain.model.Email
import cz.lastaapps.api.core.domain.model.PhoneNumber
import cz.lastaapps.core.domain.error.CommonError
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.theme.Padding
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ContactList(
    contactList: ImmutableList<Contact>,
    onError: (DomainError) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    ContactList(
        contactList = contactList,
        modifier = modifier,
        onMakePhoneCall = { contact ->
            contact.phone?.let {
                makePhoneCall(context, it) {
                    onError(CommonError.AppNotFound.PhoneCall)
                }
            }
        },
        onSendEmail = { contact ->
            contact.email?.let {
                sendEmail(context, it) {
                    onError(CommonError.AppNotFound.Email)
                }
            }
        },
        onAddContact = { contact ->
            addContact(context, contact) {
                onError(CommonError.AppNotFound.AddContact)
            }
        },
    )
}

@Composable
private fun ContactList(
    contactList: ImmutableList<Contact>,
    onMakePhoneCall: (Contact) -> Unit,
    onSendEmail: (Contact) -> Unit,
    onAddContact: (Contact) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (contactList.isNotEmpty()) {
        Column(
            modifier = modifier.width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.info_contacts_title),
                style = MaterialTheme.typography.titleLarge
            )
            contactList.forEach {
                ContactItem(
                    contact = it,
                    modifier = Modifier.fillMaxWidth(),
                    onMakePhoneCall = onMakePhoneCall,
                    onSendEmail = onSendEmail,
                    onAddContact = onAddContact,
                )
            }
        }
    }
}

@Composable
private fun ContactItem(
    contact: Contact,
    onMakePhoneCall: (Contact) -> Unit,
    onSendEmail: (Contact) -> Unit,
    onAddContact: (Contact) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (contact.name == null && contact.role == null)
        return

    ElevatedCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(Padding.MidSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            contact.name?.let { name ->
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            contact.role?.let { role ->
                Text(
                    text = role,
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            contact.phone?.let {
                OutlinedButton(
                    onClick = { onMakePhoneCall(contact) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Padding.Small),
                    ) {
                        Icon(Icons.Default.Call, null)
                        Text(text = it.number)
                    }
                }
            }

            contact.email?.let {
                OutlinedButton(
                    onClick = { onSendEmail(contact) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Padding.Small),
                    ) {
                        Icon(Icons.Default.Mail, null)
                        Text(text = it.mail)
                    }
                }
            }

            if ((contact.phone ?: contact.email) != null) {
                OutlinedButton(
                    onClick = { onAddContact(contact) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Padding.Small),
                    ) {
                        Icon(Icons.Default.Contacts, null)
                        Text(text = stringResource(R.string.info_contacts_contact_button_add))
                    }
                }
            }
        }
    }
}

private fun makePhoneCall(context: Context, phoneNumber: PhoneNumber, onError: () -> Unit) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:${Uri.encode(phoneNumber.number)}")
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

        val name = contact.name ?: contact.role ?: return
        putExtra(ContactsContract.Intents.Insert.NAME, name)

        contact.role?.let {
            putExtra(ContactsContract.Intents.Insert.JOB_TITLE, it)
        }
        contact.phone?.let {
            putExtra(ContactsContract.Intents.Insert.PHONE, it.number)
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
