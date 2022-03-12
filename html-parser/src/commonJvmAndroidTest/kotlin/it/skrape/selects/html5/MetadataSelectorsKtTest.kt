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

package it.skrape.selects.html5

import aSelfClosingTag
import aStandardTag
import aValidDocument
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isEqualTo

class MetadataSelectorsKtTest {

    @Test
    fun `can parse base-tag`() {
        val selector = aValidDocument(aSelfClosingTag("base")).base {
            findFirst {
                expectThat(attribute("custom-attr")).isEqualTo("base-attr")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("base")
    }

    @Test
    fun `can parse head-tag`() {
        val selector = aValidDocument().head {
            findFirst {
                expectThat(html).contains("<title>i'm the title</title>")
                expectThat(outerHtml).contains("<title>i'm the title</title>")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("head")
    }


    @Test
    fun `can parse link-tag`() {
        val selector = aValidDocument(aSelfClosingTag("link")).link {
            findAll {
                expectThat(attribute("custom-attr")).isEqualTo("link-attr")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("link")
    }

    @Test
    fun `can parse meta-tag`() {
        val selector = aValidDocument(aSelfClosingTag("meta")).meta {
            findAll {
                expectThat(attribute("custom-attr")).isEqualTo("meta-attr")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("meta")
    }

    @Test
    fun `can parse style-tag`() {
        val selector = aValidDocument().style {
            findFirst {
                expectThat(toString()).contains(".top-bar{margin-top")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("style")
    }

    @Test
    fun `can parse title-tag`() {
        val selector = aValidDocument(aStandardTag("title")).title {
            findFirst {
                expectThat(text).isEqualTo("i'm the title")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("title")
    }
}