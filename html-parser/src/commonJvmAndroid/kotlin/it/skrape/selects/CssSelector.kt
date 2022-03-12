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

@Suppress("LongParameterList")
@SkrapeItDsl
public class CssSelector(
    public var rawCssSelector: String = "",
    public var withClass: CssClassName? = null,
    public var withId: String? = null,
    public var withAttributeKey: String? = null,
    public var withAttributeKeys: List<String>? = null,
    public var withAttribute: Pair<String, String>? = null,
    public var withAttributes: List<Pair<String, String>>? = null,
    public val doc: CssSelectable = Doc(Document(""))
) : CssSelectable() {
    override val toCssSelector: String
        get() = ("${doc.toCssSelector} $this").trim()

    override fun applySelector(rawCssSelector: String): List<DocElement> =
        doc.applySelector("$this $rawCssSelector".trim())

    override fun toString(): String = rawCssSelector.trim() + buildString {
        append(withId.toIdSelector())
        append(withClass.toClassesSelector())
        append(withAttributeKey.toAttributeKeySelector())
        append(withAttributeKeys.toAttributesKeysSelector())
        append(withAttribute.toAttributeSelector())
        append(withAttributes.toAttributesSelector())
    }

    private fun String?.toIdSelector() = this?.let { "#$it" }.orEmpty().withoutSpaces()

    private fun CssClassName?.toClassesSelector() = this?.let { ".$it" }.orEmpty().withoutSpaces()

    private fun String?.toAttributeKeySelector() = this?.let { "[$it]" }.orEmpty().withoutSpaces()

    private fun List<String>?.toAttributesKeysSelector() =
        this?.joinToString(prefix = "['", separator = "']['", postfix = "']").orEmpty()
            .withoutSpaces()

    private fun Pair<String, String>?.toAttributeSelector() =
        this?.let { "[${it.first.withoutSpaces()}='${it.second}']" }.orEmpty()

    private fun List<Pair<String, String>>?.toAttributesSelector() =
        this?.joinToString(separator = "") { it.toAttributeSelector() }.orEmpty()

    private fun String.withoutSpaces() = replace("\\s".toRegex(), "")
}

public typealias CssClassName = String

public infix fun CssClassName.and(value: String): String = "$this.$value"

public infix fun Pair<String, String>.and(pair: Pair<String, String>): MutableList<Pair<String, String>> =
    mutableListOf(this).apply { add(pair) }
