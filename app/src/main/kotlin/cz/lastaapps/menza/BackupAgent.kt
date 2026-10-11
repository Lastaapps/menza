/*
 *    Copyright 2026, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza

import android.app.backup.BackupAgent
import android.app.backup.BackupAgentHelper
import android.app.backup.BackupDataInput
import android.app.backup.BackupDataOutput
import android.os.ParcelFileDescriptor
import android.util.Log
import cz.lastaapps.api.main.domain.usecase.wallet.WalletRestoreCredentialsUC
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin

class AppRestoreBackupAgent : BackupAgentHelper() {
    override fun onRestoreFinished() {
        super.onRestoreFinished()
        Log.d("BackupAgent", "onRestoreFinished triggered. Executing silent restoration.")

        val koin =
            startKoin {
                setupKoinApplication(applicationContext)
            }

        runBlocking {
            val walletRestoreUC = koin.koin.get<WalletRestoreCredentialsUC>()
            walletRestoreUC()
        }
    }
}

class AppBackupAgent : BackupAgent() {
    override fun onBackup(
        oldState: ParcelFileDescriptor?,
        data: BackupDataOutput?,
        newState: ParcelFileDescriptor?,
    ) {
    }

    override fun onRestore(
        data: BackupDataInput?,
        appVersionCode: Int,
        newState: ParcelFileDescriptor?,
    ) {
    }

    override fun onRestoreFinished() {
        super.onRestoreFinished()
    }
}
