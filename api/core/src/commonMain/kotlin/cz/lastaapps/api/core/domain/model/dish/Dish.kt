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

package cz.lastaapps.api.core.domain.model.dish

import cz.lastaapps.api.core.domain.model.DataLanguage
import cz.lastaapps.api.core.domain.model.DataLanguage.Czech
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.rating.Rating
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class Dish(
    val menza: MenzaType,
    val id: DishID,
    val language: DataLanguage,
    val amount: String?,
    val name: String,
    val priceDiscounted: Float?,
    val priceNormal: Float?,
    // empty - no allergens
    // null  - unknown
    val allergens: ImmutableList<Int>?,
    val photoLink: String?,
    val pictogram: ImmutableList<String>,
    val servingPlaces: ImmutableList<ServingPlace>,
    val ingredients: ImmutableList<String>,
    val isActive: Boolean,
    val rating: Rating = Rating.empty,
) {
    @Suppress("SpellCheckingInspection")
    object Mock {
        val soupSekerkova =
            Dish(
                menza = MenzaType.Testing.Kocourkov,
                id = DishID.testing(),
                language = Czech,
                amount = null,
                name = "Sekerková",
                priceDiscounted = 42.0f,
                priceNormal = 69.0f,
                allergens = persistentListOf(1, 2, 3),
                photoLink = null,
                pictogram = persistentListOf(),
                servingPlaces =
                    persistentListOf(
                        ServingPlace("Radnice", "R"),
                    ),
                ingredients = persistentListOf(),
                isActive = true,
            )
        val soupJezeckova =
            Dish(
                menza = MenzaType.Testing.Kocourkov,
                id = DishID.testing(),
                language = Czech,
                amount = null,
                name = "Ježečková",
                priceDiscounted = 42.0f,
                priceNormal = 69.0f,
                allergens = persistentListOf(1, 2, 3),
                photoLink = "https://aa.ecn.cz/img_upload/e6ffb6c50bc1424ab10ecf09e063cd63/jezek.jpg",
                pictogram = persistentListOf(),
                servingPlaces =
                    persistentListOf(
                        ServingPlace("Radnice", "R"),
                    ),
                ingredients = persistentListOf(),
                isActive = true,
            )
        val dishHnedaOmacka =
            Dish(
                menza = MenzaType.Testing.Kocourkov,
                id = DishID.testing(),
                language = Czech,
                amount = null,
                name = "Univerzální hnědá omáčka s kedlíkem",
                priceDiscounted = 42.0f,
                priceNormal = 69.0f,
                allergens = persistentListOf(1, 2, 3),
                photoLink = "https://agata.suz.cvut.cz/jidelnicky/showfotoG.php?clPodsystem=1&xFile=IMG-20240712075000098.jpg",
                pictogram = persistentListOf(),
                servingPlaces =
                    persistentListOf(
                        ServingPlace("Radnice", "R"),
                    ),
                ingredients = persistentListOf(),
                isActive = true,
            )
        val dishKuratko =
            Dish(
                menza = MenzaType.Testing.Kocourkov,
                id = DishID.testing(),
                language = Czech,
                amount = "1 kg",
                name = "Zalité kuřátko a hrany",
                priceDiscounted = 42.0f,
                priceNormal = 69.0f,
                allergens = persistentListOf(),
                photoLink = "https://agata.suz.cvut.cz/jidelnicky/showfotoG.php?clPodsystem=1&xFile=IMG-20240712075130700.jpg",
                pictogram = persistentListOf(),
                servingPlaces =
                    persistentListOf(
                        ServingPlace("Radnice", "R"),
                    ),
                ingredients = persistentListOf(),
                isActive = true,
            )
        val dishPtacek =
            Dish(
                menza = MenzaType.Testing.Kocourkov,
                id = DishID.testing(),
                language = Czech,
                amount = null,
                name = "Moravsko-španělský ptáček",
                priceDiscounted = 42.0f,
                priceNormal = 69.0f,
                allergens = persistentListOf(),
                photoLink = "https://agata.suz.cvut.cz/jidelnicky/showfotoG.php?clPodsystem=1&xFile=IMG-20240712075239254.jpg",
                pictogram = persistentListOf(),
                servingPlaces =
                    persistentListOf(
                        ServingPlace("Radnice", "R"),
                    ),
                ingredients = persistentListOf(),
                isActive = true,
            )
        val dishNoky =
            Dish(
                menza = MenzaType.Testing.Kocourkov,
                id = DishID.testing(),
                language = Czech,
                amount = "12.5 ks",
                name = "Pelíškovské noky",
                priceDiscounted = 42.0f,
                priceNormal = 69.0f,
                allergens = persistentListOf(1, 2, 3),
                photoLink = "https://agata.suz.cvut.cz/jidelnicky/showfotoG.php?clPodsystem=1&xFile=IMG-20240712075304557.jpg",
                pictogram = persistentListOf(),
                servingPlaces =
                    persistentListOf(
                        ServingPlace("Radnice", "R"),
                    ),
                ingredients = persistentListOf(),
                isActive = true,
            )
        val dishTux =
            Dish(
                menza = MenzaType.Testing.Kocourkov,
                id = DishID.testing(),
                language = Czech,
                amount = null,
                name = "Tuxík na přírodno",
                priceDiscounted = 42.0f,
                priceNormal = 69.0f,
                allergens = persistentListOf(1, 2, 3),
                photoLink = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/35/Tux.svg/800px-Tux.svg.png",
                pictogram = persistentListOf(),
                servingPlaces =
                    persistentListOf(
                        ServingPlace("Radnice", "R"),
                    ),
                ingredients = persistentListOf(),
                isActive = true,
            )
        val dishKunda =
            Dish(
                menza = MenzaType.Testing.Kocourkov,
                id = DishID.testing(),
                language = Czech,
                amount = "1 kg",
                name = "Strahovská kunda",
                priceDiscounted = 42.0f,
                priceNormal = 69.0f,
                allergens = persistentListOf(1, 2, 3),
                photoLink = @Suppress("ktlint:standard:max-line-length")
                "https://www.bydlimekvalitne.cz/sites/default/files/styles/image_840x400/public/bigstock-czech-smelly-cheese-olomouck-86998046.jpg?itok=WzhMUZt2&c=291a4bb2386048f3eb113e3ec11ccfb2",
                pictogram = persistentListOf(),
                servingPlaces =
                    persistentListOf(
                        ServingPlace("Radnice", "R"),
                    ),
                ingredients = persistentListOf(),
                isActive = true,
            )
        val dishMaxipes =
            Dish(
                menza = MenzaType.Testing.Kocourkov,
                id = DishID.testing(),
                language = Czech,
                amount = "200 g",
                name = "Štěkanátky z maxipsa Fíka",
                priceDiscounted = 42.0f,
                priceNormal = 69.0f,
                allergens = persistentListOf(1, 2, 3),
                photoLink = "https://www.czecot.cz/results/zobrobr.php?w=ac&id=201171&orig=1",
                pictogram = persistentListOf(),
                servingPlaces =
                    persistentListOf(
                        ServingPlace("Radnice", "R"),
                    ),
                ingredients = persistentListOf(),
                isActive = true,
            )
        val desertKrtkuvDort =
            Dish(
                menza = MenzaType.Testing.Kocourkov,
                id = DishID.testing(),
                language = Czech,
                amount = null,
                name = "Krtkův dort",
                priceDiscounted = 42.0f,
                priceNormal = 69.0f,
                allergens = persistentListOf(1, 2, 3),
                photoLink = "https://1gr.cz/fotky/idnes/22/121/r7/JVE86eb72_32738_102382307.jpg",
                pictogram = persistentListOf(),
                servingPlaces =
                    persistentListOf(
                        ServingPlace("Radnice", "R"),
                    ),
                ingredients = persistentListOf(),
                isActive = true,
            )
    }
}

data class ServingPlace(
    val name: String,
    val abbrev: String,
)
