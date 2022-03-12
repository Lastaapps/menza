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

import aValidDocument
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TableSelectorsKtTest {

    @Test
    fun `can parse caption-tag`() {
        val selector = aValidDocument().caption {
            findFirst {
                expectThat(text).isEqualTo("i'm the caption")
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("caption")
    }

    @Test
    fun `can parse col-tag`() {
        val selector = aValidDocument().col {
            findAll {
                expectThat(this.size).isEqualTo(2)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("col")
    }

    @Test
    fun `can parse colgroup-tag`() {
        val selector = aValidDocument().colgroup {
            findAll {
                expectThat(this.size).isEqualTo(1)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("colgroup")
    }

    @Test
    fun `can parse table-tag`() {
        val selector = aValidDocument().table {
            findAll {
                expectThat(this.size).isEqualTo(1)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("table")
    }

    @Test
    fun `can parse tbody-tag`() {
        val selector = aValidDocument().tbody {
            findAll {
                expectThat(this.size).isEqualTo(1)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("tbody")
    }

    @Test
    fun `can parse td-tag`() {
        val selector = aValidDocument().td {
            findAll {
                expectThat(this.size).isEqualTo(6)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("td")
    }

    @Test
    fun `can parse tfoot-tag`() {
        val selector = aValidDocument().tfoot {
            findAll {
                expectThat(this.size).isEqualTo(1)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("tfoot")
    }

    @Test
    fun `can parse th-tag`() {
        val selector = aValidDocument().th {
            findAll {
                expectThat(this.size).isEqualTo(2)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("th")
    }

    @Test
    fun `can parse thead-tag`() {
        val selector = aValidDocument().thead {
            findAll {
                expectThat(this.size).isEqualTo(1)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("thead")
    }

    @Test
    fun `can parse tr-tag`() {
        val selector = aValidDocument().tr {
            findAll {
                expectThat(this.size).isEqualTo(4)
            }
            toCssSelector
        }

        expectThat(selector).isEqualTo("tr")
    }
}