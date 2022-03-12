/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
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

package it.skrape.selects

import it.skrape.SkrapeItDsl
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

@Suppress("TooManyFunctions")
@SkrapeItDsl
public class Doc(public val document: Document, override var relaxed: Boolean = false) :
    DomTreeElement() {
    override val element: Element
        get() = this.document

    /**
     * Get the (unencoded) text of all children of this element, including any newlines and spaces present in the
     * original.
     *
     * @return unencoded, un-normalized text
     * @see text
     */
    public val wholeText: String by lazy { document.wholeText().orEmpty() }

    public val titleText: String by lazy { document.title().orEmpty() }

    override val toCssSelector: String = ""

    override fun makeDefaultElement(cssSelector: String): DocElement {
        return DocElement(Element(cssSelector), relaxed)
    }
}
