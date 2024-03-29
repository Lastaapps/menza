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

package it.skrape.selects

import aValidDocument
import java.awt.SystemColor.text
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo

class ElementExtractorsKtTest {

    private val document = aValidDocument { this }

    @Test
    fun `can pick elements firstOccurrence`() {
        val firstText = document.p {
            findFirst {
                text.also { expectThat(it).isEqualTo("i'm a paragraph") }
            }
        }
        expectThat(firstText).isEqualTo("i'm a paragraph")
    }

    @Test
    fun `can pick own element text only`() {
        val firstText = document.div {
            withClass = "with-children"
            findFirst {
                ownText.also { expectThat(it).isEqualTo("i'm a parent div") }
            }
        }
        expectThat(firstText).isEqualTo("i'm a parent div")
    }

    @Test
    fun `can pick elements secondOccurrence`() {
        val secondText = document.p {
            findSecond {
                text.also { expectThat(it).isEqualTo("i'm a second paragraph") }
            }
        }
        expectThat(secondText).isEqualTo("i'm a second paragraph")
    }

    @Test
    fun `can pick elements by index`() {
        val secondText = document.p {
            findByIndex(1) {
                text.also { expectThat(it).isEqualTo("i'm a second paragraph") }
            }
        }
        expectThat(secondText).isEqualTo("i'm a second paragraph")
    }

    @Test
    fun `can pick elements by index via invoke index`() {
        val secondText = document.p {
            1 {
                text.also { expectThat(it).isEqualTo("i'm a second paragraph") }
            }
        }
        expectThat(secondText).isEqualTo("i'm a second paragraph")
    }

    @Test
    fun `can pick elements thirdOccurrence`() {
        val thirdText = document.p {
            findThird {
                text.also { expectThat(it).isEqualTo("i'm a paragraph with word break") }
            }
        }
        expectThat(thirdText).isEqualTo("i'm a paragraph with word break")
    }

    @Test
    fun `can pick elements lastOccurrence`() {
        val lastText = document.p {
            findLast {
                text.also { expectThat(it).isEqualTo("i'm the last paragraph") }
            }
        }
        expectThat(lastText).isEqualTo("i'm the last paragraph")
    }

    @Test
    fun `can pick elements secondlastOccurrence`() {
        val secondLastText = document.p {
            findSecondLast {
                text.also { expectThat(it).isEqualTo("i'm a paragraph with word break") }
            }
        }
        expectThat(secondLastText).isEqualTo("i'm a paragraph with word break")
    }

    @Test
    fun `can pick element with cascading selector on table - foot`() {
        val pickedElementText = document.table {
            tfoot {
                tr {
                    td {
                        findSecond {
                            text.also { expectThat(it).isEqualTo("second foot td") }
                        }
                    }
                }
            }
        }
        expectThat(pickedElementText).isEqualTo("second foot td")
    }

    @Test
    fun `can pick element with cascading selector on table - head`() {
        val pickedElementText = document.table {
            thead {
                tr {
                    th {
                        findFirst {
                            text.also { expectThat(it).isEqualTo("first th") }
                        }
                    }
                }
            }
        }
        expectThat(pickedElementText).isEqualTo("first th")
    }

    @Test
    fun `can pick element with cascading selector on table - body`() {
        val pickedElementText = document.table {
            tbody {
                tr {
                    findSecond {
                        findFirst("td") {
                            text
                        }
                    }
                }
            }
        }
        expectThat(pickedElementText).isEqualTo("barfoo")
    }

    @Test
    fun `can pick element with cascading selector on table - colgroup`() {
        val pickedElementText = document.table {
            colgroup {
                col {
                    withAttributeKey = "span"
                    findFirst {
                        attribute("span")
                    }
                }
            }
        }
        expectThat(pickedElementText).isEqualTo("2")
    }

    @Test
    fun `can pick element by css selector matching regex`() {
        val someRegex = "^(ol|ul).*navigation$".toRegex()

        aValidDocument {
            findBySelectorMatching(someRegex) {
                expectThat(map { it.toCssSelector }).containsExactly(
                    "html > body > header > nav > ol.ordered-navigation",
                    "html > body > header > nav > ul.unordered-navigation",
                )
            }
        }
    }

    @Test
    fun `can pick element by css selector matching regex DSL invoke`() {
        val someRegex = "^(ol|ul).*navigation$".toRegex()

        aValidDocument {
            someRegex {
                expectThat(map { it.toCssSelector }).containsExactly(
                    "html > body > header > nav > ol.ordered-navigation",
                    "html > body > header > nav > ul.unordered-navigation",
                )
            }
        }
    }
}
