package cz.lastaapps.menza.ui.components

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cz.lastaapps.menza.R
import cz.lastaapps.menza.util.AgataWalletCredentials
import cz.lastaapps.scraping.AgataWallet
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AgataWalletButton() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val buttonText = remember { mutableStateOf(context.resources.getString(R.string.wallet_login)) }
    val loading = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf(false) }

    // Preview
    if (LocalInspectionMode.current) {
        buttonText.value = "0.0 Kč"
        loading.value = true
        error.value = true
    }

    // Set the button text to formatted balance
    fun setBalanceText(balance: Float) {
        buttonText.value = String.format("%.2f Kč", balance)
    }

    // Update the balance
    fun update(force: Boolean = false) {
        // Credentials
        val saved = AgataWalletCredentials.getSavedCredentials(context) ?: return
        error.value = false

        scope.launch {
            // Try cached
            val cached = AgataWalletCredentials.getCachedBalance(context)
            if (!force && cached != null) {
                setBalanceText(cached);
                return@launch
            }

            // Fetch
            loading.value = true
            buttonText.value = ""
            try {
                val agataWallet = AgataWallet()
                val balance = agataWallet.getBalance(saved.first, saved.second)
                setBalanceText(balance)
                AgataWalletCredentials.cacheBalance(context, balance)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "${context.resources.getString(R.string.wallet_update_error)}: $e", Toast.LENGTH_LONG).show()
                error.value = true
            }
            loading.value = false
        }
    }

    // Login dialog
    val openDialog = remember { mutableStateOf(false) }
    if (openDialog.value) {
        AgataLoginDialog(
            onDismissRequest = {
                openDialog.value = false
                if (it) {
                    update(true)
                }
            }
        )
    }

    // Update on start
    if (!LocalInspectionMode.current) {
        LaunchedEffect(Unit) {
            update()
        }
    }

    // Using card because buttons couldn't do long tap
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),

        // Handle clicks
        modifier = Modifier.combinedClickable(
            // Open dialog if no credentials
            onClick = {
                val saved = AgataWalletCredentials.getSavedCredentials(context)
                if (saved == null) {
                    openDialog.value = true
                    return@combinedClickable
                }
                update(true)
            },
            // Open dialog on long press
            onLongClick = {
                openDialog.value = true
            }
        ),

        // Balance || Loading || Error
        content = {
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    buttonText.value,
                    // 18sp == 24dp in preview, to match the icon & progress
                    // for some god forsaken reason dp doesn't work here
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                if (loading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp).width(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        trackColor = MaterialTheme.colorScheme.primary
                    )
                }
                if (error.value) {
                    Icon(
                        Icons.Rounded.Warning,
                        contentDescription = stringResource(R.string.wallet_update_error)
                    )
                }
            }

        }
    )

}

@Preview(showBackground = true)
@Composable
fun AgataWalletButtonPreview() {
    AgataWalletButton()
}


@Composable
fun AgataLoginDialog(
    onDismissRequest: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Load initial username & password
    if (!LocalInspectionMode.current) {
        val saved = AgataWalletCredentials.getSavedCredentials(context)
        if (saved != null) {
            username = saved.first
            password = saved.second
        }
    }


    Dialog(onDismissRequest = { onDismissRequest(false) }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.wallet_login_title),
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(R.string.wallet_login_subtitle),
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(
                    modifier = Modifier.padding(8.dp)
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(stringResource(R.string.wallet_login_username)) }
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.wallet_login_password)) },
                    visualTransformation = PasswordVisualTransformation()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismissRequest(false) },
                    ) {
                        Text(stringResource(R.string.wallet_login_cancel))
                    }
                    TextButton(
                        onClick = {
                            AgataWalletCredentials.saveCredentials(context, username, password)
                            onDismissRequest(true)
                        },
                        enabled = username.isNotBlank() && password.isNotBlank()
                    ) {
                        Text(stringResource(R.string.wallet_login_save))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AgataLoginDialogPreview() {
    AgataLoginDialog {}
}