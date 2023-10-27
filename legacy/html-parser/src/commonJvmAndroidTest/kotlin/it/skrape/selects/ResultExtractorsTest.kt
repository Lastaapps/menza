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

import it.skrape.core.htmlDocument
import it.skrape.selects.html5.p
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo

class ResultExtractorsTest {

    val expectedValue = "i'm a paragraph"

    @Language("HTML")
    val htmlSnippet = """
        <p>$expectedValue</p>
        <p>foo</p>
    """.trimIndent()

    @Test
    fun `will throw custom exception if element could not be found via element function`() {
        expectThrows<ElementNotFoundException> {
            htmlDocument(htmlSnippet) {
                findAll(".nonExistent")
            }
        }
    }

    @Test
    fun `can pick elements via select functions`() {
        htmlDocument(htmlSnippet) {
            p {
                findFirst {
                    expectThat(text).isEqualTo(expectedValue)
                }
            }
        }
    }
}
