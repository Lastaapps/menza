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

class WebComponentsSelectorsKtTest {

    @Test
    fun `can parse content-tag`() {
        val selector = aValidDocument(aStandardTag("content")).content {
            findFirst {
                expectThat(text).isEqualTo("i'm a content")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("content")
    }

    @Test
    fun `can parse shadow-tag`() {
        val selector = aValidDocument(aStandardTag("shadow")).shadow {
            findFirst {
                expectThat(text).isEqualTo("i'm a shadow")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("shadow")
    }

    @Test
    fun `can parse slot-tag`() {
        val selector = aValidDocument(aStandardTag("slot")).slot {
            findFirst {
                expectThat(text).isEqualTo("i'm a slot")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("slot")
    }

    @Test
    fun `can parse template-tag`() {
        val selector = aValidDocument(aStandardTag("template")).template {
            findFirst {
                expectThat(text).isEqualTo("i'm a template")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("template")
    }
}