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

package it.skrape.selects.html5

import aSelfClosingTag
import aValidDocument
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ScriptingSelectorsKtTest {

    @Test
    fun `can parse script-tag`() {
        val selector = aValidDocument(aSelfClosingTag("script")).script {
            findAll {
                expectThat(attribute("custom-attr")).isEqualTo("script-attr")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("script")
    }

    @Test
    fun `can parse canvas-tag`() {
        val selector = aValidDocument(aSelfClosingTag("canvas")).canvas {
            findFirst {
                expectThat(attribute("custom-attr")).isEqualTo("canvas-attr")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("canvas")
    }

    @Test
    fun `can parse noscript-tag`() {
        val selector = aValidDocument(aSelfClosingTag("noscript")).noscript {
            findAll {
                expectThat(attribute("custom-attr")).isEqualTo("noscript-attr")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("noscript")
    }
}
