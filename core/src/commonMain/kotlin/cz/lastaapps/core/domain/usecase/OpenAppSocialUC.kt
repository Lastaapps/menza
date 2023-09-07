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

package cz.lastaapps.core.domain.usecase

import cz.lastaapps.core.domain.model.AppSocial
import cz.lastaapps.core.domain.model.AppSocial.EMAIL
import cz.lastaapps.core.domain.model.AppSocial.FACEBOOK
import cz.lastaapps.core.domain.model.AppSocial.GITHUB_DEVELOPER
import cz.lastaapps.core.domain.model.AppSocial.GITHUB_ISSUES
import cz.lastaapps.core.domain.model.AppSocial.GITHUB_RELEASES
import cz.lastaapps.core.domain.model.AppSocial.GITHUB_REPO
import cz.lastaapps.core.domain.model.AppSocial.MATRIX
import cz.lastaapps.core.domain.model.AppSocial.PLAY_STORE_APP
import cz.lastaapps.core.domain.model.AppSocial.PLAY_STORE_DEVELOPER
import cz.lastaapps.core.domain.model.AppSocial.TELEGRAM
import cz.lastaapps.core.util.providers.LinkOpener

class OpenAppSocialUC internal constructor(
    private val link: LinkOpener,
) {
    operator fun invoke(social: AppSocial) =
        when (social) {
            EMAIL -> link.writeEmail("krasik.peta@seznam.cz")
            FACEBOOK -> link.openFacebookPage("https://www.facebook.com/lastaapps/")
            GITHUB_ISSUES -> link.openLink("https://github.com/Lastaapps/menza/issues")
            GITHUB_DEVELOPER -> link.openLink("https://github.com/lastaapps/")
            GITHUB_RELEASES -> link.openLink("https://github.com/Lastaapps/menza/releases")
            GITHUB_REPO -> link.openLink("https://github.com/Lastaapps/menza")
            MATRIX -> link.openLink("https://matrix.to/#/#lastaapps_menza:matrix.org")
            PLAY_STORE_APP -> link.openLink("https://play.google.com/store/apps/details?id=cz.lastaapps.menza")
            PLAY_STORE_DEVELOPER -> link.openLink("https://play.google.com/store/apps/developer?id=Lasta+apps")
            TELEGRAM -> link.openTelegram("https://t.me/lasta_apps")
        }
}
