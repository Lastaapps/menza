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

package cz.lastaapps.api.main.domain.usecase

import cz.lastaapps.api.core.domain.sync.getData
import cz.lastaapps.api.core.domain.sync.sync
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.api.core.domain.model.MenzaType.Agata.Strahov
import cz.lastaapps.api.core.domain.model.MenzaType.Agata.Subsystem
import cz.lastaapps.api.core.domain.model.MenzaType.Buffet.FEL
import cz.lastaapps.api.core.domain.model.MenzaType.Buffet.FS
import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import cz.lastaapps.core.util.providers.LinkOpener

class OpenMenuUC internal constructor(
    context: UCContext,
    private val linkOpener: LinkOpener,
) : UseCase(context) {
    suspend operator fun invoke(menza: Menza) = launch {
        val url = when (val type = menza.type) {
            Strahov -> "https://agata.suz.cvut.cz/jidelnicky/restauraceSH.php"
            is Subsystem -> "https://agata.suz.cvut.cz/jidelnicky/?clPodsystem=${type.subsystemId}"
            FEL, FS -> "http://studentcatering.cz/jidelni-listek/"
        }

        linkOpener.openLink(url)
    }
}
