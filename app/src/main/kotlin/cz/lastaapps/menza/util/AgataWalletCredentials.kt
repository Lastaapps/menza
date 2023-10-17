package cz.lastaapps.menza.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AgataWalletCredentials {
    companion object {
        /// Get encrypted shared preferences to store username & password
        private fun getSharedPreferences(context: Context): SharedPreferences {
            val masterKey =
                MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
            return EncryptedSharedPreferences.create(
                context,
                "agata",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

        /// Return saved username & password
        fun getSavedCredentials(context: Context): Pair<String, String>? {
            val sharedPreferences = getSharedPreferences(context)
            val username = sharedPreferences.getString("username", null)
            val password = sharedPreferences.getString("password", null)
            if (username == null || password == null) {
                return null
            }
            return Pair(username, password)
        }

        /// Save credentials to shared preferences
        fun saveCredentials(context: Context, username: String, password: String) {
            getSharedPreferences(context).edit {
                putString("username", username)
                putString("password", password)
            }
        }

        /// Add balance to cache
        fun cacheBalance(context: Context, balance: Float) {
            getSharedPreferences(context).edit {
                putFloat("balance", balance)
                putLong("balanceAge", System.currentTimeMillis())
            }
        }

        /// Get balance from cache
        fun getCachedBalance(context: Context): Float? {
            val invalid = -99999f
            val sharedPreferences = getSharedPreferences(context)
            val balance = sharedPreferences.getFloat("balance", invalid)
            val balanceAge = sharedPreferences.getLong("balanceAge", 0)
            // 5 minutes expiration
            if (balance == invalid || (System.currentTimeMillis() - balanceAge) > 300_000) {
                return null
            }
            return balance
        }
    }
}