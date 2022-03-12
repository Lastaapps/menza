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
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class InteractiveSelectorsKtTest {

    @Test
    fun `can parse details-tag`() {
        val selector = aValidDocument(aStandardTag("details")).details {
            findFirst {
                expectThat(text).isEqualTo("i'm a details")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("details")
    }

    @Test
    fun `can parse dialog-tag`() {
        val selector = aValidDocument(aStandardTag("dialog")).dialog {
            findFirst {
                expectThat(text).isEqualTo("i'm a dialog")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("dialog")
    }

    @Test
    fun `can parse menu-tag`() {
        val selector = aValidDocument(aStandardTag("menu")).menu {
            findFirst {
                expectThat(text).isEqualTo("i'm a menu")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("menu")
    }

    @Test
    fun `can parse menuitem-tag`() {
        val selector = aValidDocument(aStandardTag("menuitem")).menuitem {
            findFirst {
                expectThat(text).isEqualTo("i'm a menuitem")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("menuitem")
    }

    @Test
    fun `can parse summary-tag`() {
        val selector = aValidDocument(aStandardTag("summary")).summary {
            findFirst {
                expectThat(text).isEqualTo("i'm a summary")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("summary")
    }
}