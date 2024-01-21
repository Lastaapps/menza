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

package cz.lastaapps.menza.features.main.domain.usecase

import android.os.Build
import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import cz.lastaapps.core.util.extensions.localLogger

/**
 * Checks if the device is Galaxy Flip 3 or 4
 */
class IsFlipUC internal constructor(
    context: UCContext,
) : UseCase(context) {

        private val log = localLogger()

    operator fun invoke() = run {
        Build.MODEL.also {
            log.d { "Device model: $it" }
        }.let {
            it.startsWith("SM-F711") // Z Flip 3
                    || it.startsWith("SM-F721") // Z Flip 4
        }
    }
}
