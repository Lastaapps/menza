/*
 *    Copyright 2021, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.scraping

import it.skrape.selects.CssSelectable
import it.skrape.selects.DocElement
import java.time.ZoneId

fun <T> CssSelectable.tryFindFirst(cssSelector: String = "", init: DocElement.() -> T): T? =
    try {
        findFirst(cssSelector, init)
    } catch (e: Exception) {
        null
    }

fun <T> CssSelectable.tryFindAll(cssSelector: String = "", init: List<DocElement>.() -> T): T? =
    try {
        findAll(cssSelector, init)
    } catch (e: Exception) {
        null
    }

fun <T> CssSelectable.tryFindAllAndCycle(cssSelector: String = "", init: DocElement.() -> T) =
    try {
        findAllAndCycle(cssSelector, init)
    } catch (e: Exception) {
        null
    }

internal fun <T> CssSelectable.findAllAndCycle(
    cssSelector: String = "",
    init: DocElement.() -> T
) {
    findAll(cssSelector) {
        forEach {
            with(it) {
                init()
            }
        }
    }
}

internal fun <E> Collection<E>.forEachApply(action: E.() -> Unit) {
    this.forEach {
        with(it) {
            action()
        }
    }
}

internal fun String.removeSpaces(): String =
    replace("&nbsp;", "").trim()

internal val CET get() = ZoneId.of("Europe/Prague")
