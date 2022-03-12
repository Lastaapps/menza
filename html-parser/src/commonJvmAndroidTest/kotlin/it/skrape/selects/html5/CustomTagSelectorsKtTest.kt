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

import aStandardTag
import aValidDocument
import it.skrape.selects.ElementNotFoundException
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo

class CustomTagSelectorsKtTest {

    @Test
    fun `can pick html5 custom-tag`() {
        val selector = aValidDocument(aStandardTag("custom-tag")) {
            customTag("header") {
                findFirst {
                    customTag("h1") {
                        findFirst {
                            expectThat(text).isEqualTo("i'm the headline")
                        }
                    }
                }
            }
            customTag("custom-tag") {
                findFirst {
                    expectThat(text).isEqualTo("i'm a custom-tag")
                }
                toCssSelector
            }
        }

        expectThat(selector).isEqualTo("custom-tag")

    }

    @Test
    fun `can pick html5 custom selector via invoked string`() {
        val selector = aValidDocument(aStandardTag("custom-tag")) {
            "custom-tag" {
                findFirst {
                    expectThat(text).isEqualTo("i'm a custom-tag")
                }
                toCssSelector
            }
        }
        expectThat(selector).isEqualTo("custom-tag")
    }

    @Test
    fun `can cascade custom tag selectors`() {
        val selector = aValidDocument {
            customTag("div") {
                withId = "schnitzel"
                customTag("bar") {
                    withClass = "foobar"
                    toCssSelector
                }
            }
        }
        expectThat(selector).isEqualTo("div#schnitzel bar.foobar")
    }

    @Test
    fun `can cascade custom selector from invoked string`() {
        expectThrows<ElementNotFoundException> {
            aValidDocument {
                "div" {
                    withId = "schnitzel"
                    "bar" {
                        withClass = "foobar"
                        "foo" {
                            withAttributeKey = "xxx"
                            toCssSelector
                            findFirst {}
                        }
                    }
                }
            }
        }.get { message }
            .isEqualTo("""Could not find element "div#schnitzel bar.foobar foo[xxx]"""")
    }

    @Test
    fun `cascading custom selector will return generic type`() {
        val selector = aValidDocument {
            "div" {
                withId = "schnitzel"
                "bar" {
                    withClass = "foobar"
                    "foo" {
                        withAttributeKey = "xxx"
                        listOf(toCssSelector)
                    }
                }
            }
        }
        expectThat(selector).containsExactly("div#schnitzel bar.foobar foo[xxx]")
    }
}
