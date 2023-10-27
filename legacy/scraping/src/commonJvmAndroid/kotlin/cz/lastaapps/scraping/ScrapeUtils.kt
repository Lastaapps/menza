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

package cz.lastaapps.scraping

import it.skrape.selects.CssSelectable
import it.skrape.selects.DocElement
import it.skrape.selects.ElementNotFoundException
import kotlinx.datetime.TimeZone

internal fun <T> CssSelectable.tryFindByIndex(
    index: Int,
    cssSelector: String = "",
    init: DocElement.() -> T,
): T? {
    try {
        // tests if the element exists
        findByIndex(index, cssSelector)
    } catch (e: ElementNotFoundException) {
        return null
    }
    // is placed outside of the try-catch, so Errors in the init block aren't caught
    return findByIndex(index, cssSelector, init)
}

internal fun <T> CssSelectable.tryFindFirst(
    cssSelector: String = "",
    init: DocElement.() -> T,
): T? {
    try {
        findFirst(cssSelector)
    } catch (e: ElementNotFoundException) {
        return null
    }
    return findFirst(cssSelector, init)
}

internal fun <T> CssSelectable.tryFindAll(
    cssSelector: String = "",
    init: List<DocElement>.() -> T,
): T? {
    try {
        findFirst(cssSelector)
    } catch (e: ElementNotFoundException) {
        return null
    }
    return findAll(cssSelector, init)
}

internal fun <T> CssSelectable.tryFindAllAndCycle(
    cssSelector: String = "",
    init: DocElement.() -> T,
) {
    try {
        findFirst(cssSelector)
    } catch (e: ElementNotFoundException) {
        return
    }
    return findAllAndCycle(cssSelector, init)
}

internal fun <T> CssSelectable.findAllAndCycle(
    cssSelector: String = "",
    init: DocElement.() -> T,
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

internal val CET get() = TimeZone.of("Europe/Prague")
